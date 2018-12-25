package com.bazzi.probe.test.features;

import com.bazzi.core.redis.ProbeRedis;
import com.bazzi.core.util.PropertyUtil;
import com.bazzi.probe.test.TestBase;
import org.junit.Test;

import javax.annotation.Resource;

public class TestFrameFeatures extends TestBase {

	@Resource
	private ProbeRedis probeRedis;
	@Resource
	private PropertyUtil propertyUtil;

	// @Test // 测试redis
	public void testRedisAr() {
		// probeRedis.set("myproject", "this is cluster test!");
		// print(probeRedis.get("myproject"));
		probeRedis.sadd("fruit", "apple", "banana", "cherry");
		// print(probeRedis.srandmember("fruit"));
	}

	@Test // 测试PropertyUtil
	public void testPropertyUtil() {
		print(propertyUtil.getProperty("jdbc.driverClassName"));
	}

}
