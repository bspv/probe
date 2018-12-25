package com.bazzi.core.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * 读取配置文件
 *
 * @author PanJianzang
 */
public final class PropertyUtil extends PropertyPlaceholderConfigurer {
	private static Properties properties = null;

	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
//		properties = props;
		setProps(props);
		super.processProperties(beanFactoryToProcess, props);
	}

	private static void setProps(Properties props) {
		properties = props;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

}
