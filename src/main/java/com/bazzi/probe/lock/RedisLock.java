package com.bazzi.probe.lock;

import com.bazzi.core.redis.ProbeRedis;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.LockSupport;

@Component
public final class RedisLock {
	@Resource
	private ProbeRedis probeRedis;

	private static ProbeRedis probeRedisStatic;

	@PostConstruct
	public void init() {
		if (probeRedisStatic == null)
			probeRedisStatic = probeRedis;
	}

	public static void lock(String key, int seconds) throws Exception {
		if (probeRedisStatic.setNx(key, key) == 0) {
			throw new Exception("Get Lock Fail！");
		}
		probeRedisStatic.expire(key, seconds);
	}

	public static void unlock(String key) {
		probeRedisStatic.del(key);
	}

	private static final ConcurrentMap<String, Thread> map = new ConcurrentHashMap<>();

	public static void blockLock(String key, int seconds) throws Exception {
		if (probeRedisStatic.setNx(key, key) == 1) {
			probeRedisStatic.expire(key, seconds);
			return;
		}
		if (map.containsKey(key)) {
			throw new Exception("Get Lock Fail！");
		}
		map.put(key, Thread.currentThread());
		long l = new BigDecimal(System.currentTimeMillis()).add(new BigDecimal(seconds).multiply(new BigDecimal(1000)))
				.longValue();
		LockSupport.parkUntil(l);
	}

	public static void blockRelease(String key)  {
		probeRedisStatic.del(key);
		if (map.containsKey(key)) {
			LockSupport.unpark(map.get(key));
			map.remove(key);
		}
	}

}
