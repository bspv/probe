package com.bazzi.probe.test.lock;

import com.bazzi.core.redis.ProbeRedis;
import com.bazzi.probe.test.TestBase;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestLockWithLoop extends TestBase {

	@Resource
	private ProbeRedis probeRedis;

	@Test
	public void testLockForEach() throws Exception {
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
								lockKey("1YMUW1UFPORIKRUMOWWV9ASM_24");
							else
								lockKey("1YMUW1UFPORIKRUMOWWV9ASM_25");
						} catch (Exception e) {
							e.printStackTrace();
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
		redisLock(key, 15);
		System.out.println("Get Lock----------" + Thread.currentThread().getName() + ","
				+ (System.currentTimeMillis() / 1000) + "," + key);
		try {
			if (key.endsWith("_25"))
				Thread.sleep(1000);
			else
				Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		releaseLock(key);
		System.out.println("End(" + Thread.currentThread().getName() + ")");
	}

	private void redisLock(String key, int seconds) {
		do {
			System.out.println("try-lock-(sleep)-(" + Thread.currentThread().getName() + ")");
			if (probeRedis.setNx(key, key) == 1) {
				probeRedis.expire(key, seconds);
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	private void releaseLock(String key) {
		probeRedis.del(key);
	}

}
