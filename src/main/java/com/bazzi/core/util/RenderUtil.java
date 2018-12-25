package com.bazzi.core.util;

import com.alibaba.fastjson.JSON;
import com.bazzi.core.result.Result;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 返回JSON
 * 
 * @author PanJianzang
 *
 */
@Slf4j
public final class RenderUtil {
	private static String DEFAULT_CHARSET = "UTF-8";

	public static void renderJson(HttpServletResponse response, Result<?> result) {
		renderJson(response, result, DEFAULT_CHARSET);
	}

	public static void renderJson(HttpServletResponse response, Result<?> result, String charset) {
		PrintWriter out = null;
		try {
			charset = charset == null || "".equals(charset) ? DEFAULT_CHARSET : charset;
			String json = result != null ? JSON.toJSONString(result) : "";
			response.setContentType("text/plain;charset=" + charset);
			response.setContentLength(json.getBytes(charset).length);
			out = response.getWriter();
			out.write(json);
			out.flush();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

}
