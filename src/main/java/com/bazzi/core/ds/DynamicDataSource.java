package com.bazzi.core.ds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

/**
 * 动态数据源
 * 
 * @author PanJianzang
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	/**
	 * 重写，获取所有配置数据源
	 */
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		DataSourceHandler.setTargetDataSources(targetDataSources);
		super.setTargetDataSources(targetDataSources);
	}

	/**
	 * 重写，获取默认数据源
	 */
	public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
		DataSourceHandler.setDefaultTargetDataSource(defaultTargetDataSource);
		super.setDefaultTargetDataSource(defaultTargetDataSource);
	}

	protected Object determineCurrentLookupKey() {
		return DataSourceHandler.get();
	}

	/**
	 * 初始化
	 */
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		DataSourceHandler.init();
	}

	/**
	 * 切换到从库
	 */
	public void beforeDataSourceSlave() {
		DataSourceHandler.switchToSlave();
	}

	public static void reset() {
		DataSourceHandler.reset();
	}

}
