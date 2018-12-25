package com.bazzi.probe.lock;

import com.bazzi.core.redis.ProbeRedis;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public final class RedisBQLock {
	@Resource
	private ProbeRedis probeRedis;

	private static ProbeRedis probeRedisStatic;

	@PostConstruct
	public void init() {
		if (probeRedisStatic == null)
			probeRedisStatic = probeRedis;
	}

	private static final BlockingQueue<BlockEle> bq = new ArrayBlockingQueue<>(1);

	public static void redisLock(String key, int seconds) throws Exception {
		do {
			System.out.println("try-lock-(" + Thread.currentThread().getName() + ")");
			if (probeRedisStatic.setNx(key, key) == 1) {
				probeRedisStatic.expire(key, seconds);
				break;
			}
			bq.put(new BlockEle(Thread.currentThread(), key));
		} while (true);
	}

	public static void releaseLock(String key) {
		probeRedisStatic.del(key);
		for (BlockEle blockEle : bq) {
			if (key.equals(blockEle.getKey())) {
				bq.remove(blockEle);
				break;
			}
		}
	}

}
