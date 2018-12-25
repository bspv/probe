package com.bazzi.core.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.Map;

@Slf4j
public final class FreeMarkerUtil {

	private FreeMarkerConfigurer freeMarkerConfigurer;

	private static FreeMarkerConfigurer freeMarkerConfigurerStatic;

	/**
	 * 根据FreeMarkerConfigurer里的信息，将templateName对应的模板填充object参数后生成页面，并转化为byte数组
	 * <br/>
	 * freeMarkerConfigurer属性需要初始化设置
	 *
	 * @param templateName 模板名称
	 * @param object       参数
	 * @return 文件的byte数组
	 */
	public static byte[] create(String templateName, Map<String, Object> object) {
		byte[] data = null;
		Writer out = null;
		try {
			if (freeMarkerConfigurerStatic == null)
				throw new RuntimeException("Property 'freeMarkerConfigurer' is required");
			Configuration configuration = freeMarkerConfigurerStatic.getConfiguration();
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			out = new BufferedWriter(new OutputStreamWriter(byteArray, configuration.getDefaultEncoding()));
			Template template = configuration.getTemplate(templateName);
			template.setOutputEncoding(configuration.getDefaultEncoding());
			template.process(object, out);
			data = byteArray.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
		}
		return data;
	}

	public FreeMarkerConfigurer getFreeMarkerConfigurer() {
		return freeMarkerConfigurer;
	}

	public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
		setConfigurer(freeMarkerConfigurer);
	}

	private static void setConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
		FreeMarkerUtil.freeMarkerConfigurerStatic = freeMarkerConfigurer;
	}
}
