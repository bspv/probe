package com.bazzi.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class ExportExcel {
	private static final int ROW_ACCESS_WINDOW_SIZE = 2000;

	private static final int XLS_ROW = 65536;
	private static final int XLS_COL = 256;
	private static final int XLSX_ROW = 1048576;
	private static final int XLSX_COL = 16384;

	private static final ThreadLocal<CellStyle> headerLocal = new ThreadLocal<>();
	private static final ThreadLocal<CellStyle> bodyLocal = new ThreadLocal<>();
	private static final ThreadLocal<CellStyle> totalLocal = new ThreadLocal<>();

	/**
	 * 生成Excel，并写入response的OutputStream里
	 * 
	 * @param outputStream ServletOutputStream
	 * @param colWidth     每列的宽度
	 * @param title        标题，如果为空则不生成标题
	 * @param header       表头，必须有，名称顺序和body中泛型T里属性的顺序需要保持一致
	 * @param body         内容
	 * @param total        总计，如果为空则不生成总计
	 * @param isXlsx       true代表生成xlsx，否则生成xls
	 */
	public static <T> void createExcel(ServletOutputStream outputStream, int[] colWidth, String title, String[] header,
			List<T> body, List<String> total, boolean isXlsx) {
		if (outputStream == null || header == null || header.length == 0)
			throw new IllegalArgumentException("Property outputStream && header is required");
		body = body == null ? new ArrayList<>() : body;
		// 判断总列数是不是超过了最大的列数限制
		int totalCol = header.length;
		if ((!isXlsx && totalCol > XLS_COL) || totalCol > XLSX_COL)
			throw new IllegalArgumentException(
					"TotalCol(" + totalCol + " > " + (isXlsx ? XLSX_COL : XLS_COL) + ") ,create Excel Fail");
		// 判断总行数是不是超过了最大的行数限制
		int totalRow = (title == null || "".equals(title) ? 0 : 1) + 1 + body.size()
				+ (total == null ? 0 : total.size());
		if ((!isXlsx && totalRow > XLS_ROW) || totalRow > XLSX_ROW)
			throw new IllegalArgumentException(
					"TotalRow(" + totalRow + " > " + (isXlsx ? XLSX_ROW : XLS_ROW) + ") ,create Excel Fail");
		Workbook wb = null;
		try {
			wb = isXlsx ? new SXSSFWorkbook(ROW_ACCESS_WINDOW_SIZE) : new HSSFWorkbook();
			Sheet sheet = wb.createSheet();
			if (colWidth != null && colWidth.length > 0) {
				for (int i = 0; i < colWidth.length; i++) {
					sheet.setColumnWidth(i, colWidth[i] * 256);
				}
			} else {
				sheet.setDefaultColumnWidth(20);
			}
			int current = createTitle(wb, sheet, title, totalCol);// 创建标题
			current = createHeader(wb, sheet, header, current);// 创建列头
			for (int rowNum = 0; rowNum < body.size(); rowNum++) {
				setRowValue(wb, sheet, sheet.createRow(rowNum + current), body.get(rowNum));
			}
			createTotal(wb, sheet, total, current + body.size(), totalCol);// 创建统计
			wb.write(outputStream);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			headerLocal.set(null);
			bodyLocal.set(null);
			totalLocal.set(null);
			if (wb != null)
				try {
					wb.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
		}
	}

	/**
	 * 创建标题，如果title字段为空，则不设置标题
	 * 
	 * @param wb       工作簿
	 * @param sheet    表格
	 * @param title    标题
	 * @param colTotal 总列数
	 * @return 有标题返回1，否则返回0
	 */
	private static int createTitle(Workbook wb, Sheet sheet, String title, int colTotal) {
		if (title != null && !"".equals(title)) {
			mergedRegion(sheet, 0, 0, 0, colTotal - 1);
			Row row = sheet.createRow(0);
			row.setHeightInPoints(30);
			Cell cell = row.createCell(0);
			cell.setCellStyle(titleStyle(wb));
			cell.setCellValue(title);
			return 1;
		}
		return 0;
	}

	/**
	 * 创建表头
	 * 
	 * @param wb      工作簿
	 * @param sheet   表格
	 * @param header  列头
	 * @param current 当前第几行
	 * @return 返回current+1
	 */
	private static int createHeader(Workbook wb, Sheet sheet, String[] header, int current) {
		Row row = sheet.createRow(current);
		row.setHeightInPoints(30);
		for (int i = 0; i < header.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(headerStyle(wb));
			cell.setCellValue(header[i]);
		}
		return current + 1;
	}

	/**
	 * 设置表体中一行记录
	 * 
	 * @param wb    工作簿
	 * @param sheet 表格
	 * @param row   行
	 * @param t     行数据
	 * @throws Exception 异常
	 */
	private static <T> void setRowValue(Workbook wb, Sheet sheet, Row row, T t) throws Exception {
		Field[] fields = t.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			if (fieldName != null && !"serialVersionUID".equals(fieldName)) {
				Cell cell = row.createCell(i);
				cell.setCellStyle(bodyStyle(wb));

				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				Method getMethod = t.getClass().getMethod(getMethodName);
				Object value = getMethod.invoke(t);
				convertValueToCell(value, cell);
			}
		}
	}

	/**
	 * 设置一个单元格的值
	 * 
	 * @param value 值
	 * @param cell  单元格
	 */
	private static void convertValueToCell(Object value, Cell cell) {
		if (value == null || "".equals(value)) {
			cell.setCellValue("");
		} else if (value instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			cell.setCellValue(sdf.format((Date) value));
		} else if (value instanceof Double) {
			Matcher matcher = Pattern.compile("^//d+(//.//d+)?$").matcher(value.toString());
			if (matcher.matches()) {
				// 是数字当作double处理
				cell.setCellValue(Double.parseDouble(value.toString()));
			} else {
				cell.setCellValue(value.toString());
			}
		} else {
			cell.setCellValue(value.toString());
		}
	}

	/**
	 * 创建总计数据，total为空则不创建
	 * 
	 * @param wb       工作簿
	 * @param sheet    表格
	 * @param total    总计内容
	 * @param rowTotal 当前已有的行数
	 * @param colTotal 列数
	 */
	private static void createTotal(Workbook wb, Sheet sheet, List<String> total, int rowTotal, int colTotal) {
		if (total != null && total.size() > 0)
			for (int i = 0; i < total.size(); i++) {
				int index = rowTotal + i;
				Row row = sheet.createRow(index);
				mergedRegion(sheet, index, index, 0, colTotal - 1);
				Cell c = row.createCell(0);
				c.setCellStyle(totalStyle(wb));
				c.setCellValue(total.get(i));
			}
	}

	/**
	 * 合并单元格
	 * 
	 * @param sheet    表格
	 * @param firstRow 起行
	 * @param lastRow  止行
	 * @param firstCol 起列
	 * @param lastCol  止列
	 */
	private static void mergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
	}

	/**
	 * 获取标题的单元格样式
	 * 
	 * @param wb 工作簿
	 * @return 单元格样式
	 */
	private static CellStyle titleStyle(Workbook wb) {
		return baseStyle(wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false, true, 12, "宋体");
	}

	/**
	 * 获取表头的单元格样式
	 * 
	 * @param wb 工作簿
	 * @return 单元格样式
	 */
	private static CellStyle headerStyle(Workbook wb) {
		CellStyle headerStyle = headerLocal.get();
		if (headerStyle == null) {
			headerStyle = baseStyle(wb, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false, false, 12, "宋体");
			headerLocal.set(headerStyle);
		}
		return headerStyle;
	}

	/**
	 * 获取表体的单元格样式
	 * 
	 * @param wb 工作簿
	 * @return 单元格样式
	 */
	private static CellStyle bodyStyle(Workbook wb) {
		CellStyle bodyStyle = bodyLocal.get();
		if (bodyStyle == null) {
			bodyStyle = baseStyle(wb, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false, false, 12, "宋体");
			bodyLocal.set(bodyStyle);
		}
		return bodyStyle;
	}

	/**
	 * 获取总计的单元格样式
	 * 
	 * @param wb 工作簿
	 * @return 单元格样式
	 */
	private static CellStyle totalStyle(Workbook wb) {
		CellStyle totalStyle = totalLocal.get();
		if (totalStyle == null) {
			totalStyle = baseStyle(wb, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, false, false, 12, "宋体");
			totalLocal.set(totalStyle);
		}
		return totalStyle;
	}

	/**
	 * 设置单元格格式
	 * 
	 * @param wb         工作簿
	 * @param hAlign     水平对齐方式
	 * @param vAlign     垂直对齐方式
	 * @param wrapFlag   是否自动折行，true折行，false不折行
	 * @param boldFlag   是否加粗，true加粗，false不加粗
	 * @param fontHeight 字体大小
	 * @param fontName   字体名称
	 * @return 单元格样式
	 */
	private static CellStyle baseStyle(Workbook wb, HorizontalAlignment hAlign, VerticalAlignment vAlign,
			boolean wrapFlag, boolean boldFlag, int fontHeight, String fontName) {
		CellStyle style = wb.createCellStyle();
		// 设置填充字体的样式
		style.setFillPattern(FillPatternType.NO_FILL);
		// 设置单元格居中对齐
		style.setAlignment(hAlign);// 水平
		style.setVerticalAlignment(vAlign);// 垂直
		style.setWrapText(wrapFlag);// 自动折行，true折行，false不折行
		// 设置单元格字体样式
		Font font = wb.createFont();
		font.setBold(boldFlag);// 字体加粗，true加粗，false不加粗
		font.setFontName(fontName);// 字体的名称
		// 将fontHeight乘以20以后再转换为short类型，缺省的字体大小是11
		short fontHeightShort = (short) ((fontHeight > (Short.MAX_VALUE / 20) ? 11 : fontHeight) * 20);
		font.setFontHeight(fontHeightShort);// 字体的大小
		style.setFont(font);// 将字体填充到表格中去

		return style;
	}

}
