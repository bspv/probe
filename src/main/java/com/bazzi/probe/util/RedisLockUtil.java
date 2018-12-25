package com.bazzi.probe.util;

import com.bazzi.core.redis.ProbeRedis;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

public final class RedisLockUtil {
	@Resource
	private ProbeRedis probeRedis;

	private static ProbeRedis probeRedisStatic;

	@PostConstruct
	public void init() {
		if (probeRedisStatic == null)
			probeRedisStatic = probeRedis;
	}

}
