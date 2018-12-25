package com.bazzi.probe.test.lock;

import com.bazzi.core.redis.ProbeRedis;
import com.bazzi.probe.test.TestBase;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

public class TestLockWithMap extends TestBase {
	@Resource
	private ProbeRedis probeRedis;

	@Test
	public void testBlockingQueue() throws Exception {
		int total = 20;
		CountDownLatch cdl = new CountDownLatch(1);
		CountDownLatch cdlAll = new CountDownLatch(total);
		ExecutorService es = Executors.newFixedThreadPool(total);
		for (int i = 0; i < total; i++) {
			final int idx = i;
			es.execute(new Runnable() {
				public void run() {
					try {
						cdlAll.countDown();
						cdl.await();

						try {
							if (idx >= 10)
								lockKey("1YMUW1UFPORIKRUMOWWV9ASM_02");
							else
								lockKey("1YMUW1UFPORIKRUMOWWV9ASM_10");
						} catch (Exception e) {
							// e.printStackTrace();
							print(e.getMessage());
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		cdlAll.await();
		print("执行二维码激活步骤！");
		cdl.countDown();
		es.shutdown();
		Thread.sleep(40000);
	}

	private void lockKey(String key) throws Exception {
		System.out.println("Start(" + Thread.currentThread().getName() + ")");
		blockLock(key, 15);
		System.out.println("Get Lock----------" + Thread.currentThread().getName() + ","
				+ (System.currentTimeMillis() / 1000) + "," + key);
		try {
			if (key.endsWith("_10"))
				Thread.sleep(1000);
			else
				Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		blockRelease(key);
		System.out.println("End(" + Thread.currentThread().getName() + ")");
	}

	private   ConcurrentMap<String, Thread> map = new ConcurrentHashMap<String, Thread>();

	public void blockLock(String key, int seconds) throws Exception {
		if (probeRedis.setNx(key, key) == 1) {
			probeRedis.expire(key, seconds);
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

	public void blockRelease(String key) throws Exception {
		probeRedis.del(key);
		if (map.containsKey(key)) {
			LockSupport.unpark(map.get(key));
			map.remove(key);
		}
	}

}
