package com.bazzi.probe.test.lock;

import com.bazzi.core.redis.ProbeRedis;
import com.bazzi.probe.test.TestBase;
import org.junit.Test;

import javax.annotation.Resource;

public class TestRedisLuaLock extends TestBase {

	@Resource
	private ProbeRedis probeRedis;

	@Test
	public void testLock() {
		String key = "order123";
		String value = "order123_value";
		boolean i = probeRedis.tryLock(key, value, 500000);
		print(i);
	}

//	@Test
	public void testReleaseLock() {
		String key = "order123";
		String value = "order123_value";
		boolean i = probeRedis.releaseLock(key, value);
		print(i);
	}
}
