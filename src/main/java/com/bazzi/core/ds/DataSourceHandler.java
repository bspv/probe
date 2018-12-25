package com.bazzi.core.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据源管理操作，切换主从，支持一主多从
 *
 * @author PanJianzang
 */
public class DataSourceHandler {
	private static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();

	private static Map<Object, Object> dsMap = null;

	private static Object defTargetDataSource = null;

	private static final List<Object> slaveList = new ArrayList<>();

	private static Object master = null;

	private static int slaveSize = 0;

	private static final AtomicInteger count = new AtomicInteger(-1);

	/**
	 * 获取默认的数据源
	 *
	 * @param defaultTargetDataSource
	 */
	public static void setDefaultTargetDataSource(Object defaultTargetDataSource) {
		defTargetDataSource = defaultTargetDataSource;
	}

	/**
	 * 获取所有配置的数据源
	 *
	 * @param map
	 */
	public static void setTargetDataSources(Map<Object, Object> map) {
		dsMap = map;
	}

	/**
	 * 初始化操作，在afterPropertiesSet后执行，将所有slave择出来
	 */
	public static void init() {
		if (dsMap == null || defTargetDataSource == null)
			throw new RuntimeException("init Error");
		for (Map.Entry<Object, Object> entry : dsMap.entrySet()) {
			if (Objects.deepEquals(entry.getValue(),defTargetDataSource)){
				master = entry.getKey();
			}else{
				slaveList.add(entry.getKey());
			}
		}
//		for (Object key : dsMap.keySet()) {
//			if (Objects.deepEquals(dsMap.get(key), defTargetDataSource))
//				master = key;
//			else
//				slaveList.add(key);
//		}
		slaveSize = slaveList.size();
	}

	public static Object get() {
		return threadLocal.get();
	}

	/**
	 * 切换到Slave，采用轮询的策略
	 */
	public static void switchToSlave() {
		if (slaveSize > 0) {
			int index = Math.abs(count.incrementAndGet()) % slaveSize;
			set(slaveList.get(index));
		}
	}

	/**
	 * 切换主库
	 */
	public static void switchToMaster() {
		set(master);
	}

	/**
	 * 重置
	 */
	public static void reset() {
		set(master);
	}

	private static void set(Object obj) {
		threadLocal.set(obj);
	}

}
