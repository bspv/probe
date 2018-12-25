package com.bazzi.probe.test.lock;

import com.bazzi.probe.lock.RedisLock;
import com.bazzi.probe.test.TestBase;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestRedisLock extends TestBase {

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
							print(e.getMessage());
						}
					} catch (InterruptedException e) {
						print(e.getMessage());
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
		RedisLock.lock(key, 15);
		System.out.println("Get Lock----------" + Thread.currentThread().getName() + ","
				+ (System.currentTimeMillis() / 1000) + "," + key);
		try {
			if (key.endsWith("_10"))
				Thread.sleep(1000);
			else
				Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		RedisLock.unlock(key);
		System.out.println("End(" + Thread.currentThread().getName() + ")");
	}

}
