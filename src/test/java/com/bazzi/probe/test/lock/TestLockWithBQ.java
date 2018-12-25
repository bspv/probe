package com.bazzi.probe.test.lock;

import com.bazzi.core.redis.ProbeRedis;
import com.bazzi.probe.lock.BlockEle;
import com.bazzi.probe.test.TestBase;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.concurrent.*;

public class TestLockWithBQ extends TestBase {

	@Resource
	private ProbeRedis probeRedis;

	private BlockingQueue<BlockEle> bq = new ArrayBlockingQueue<BlockEle>(1);

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
			if (key.endsWith("_10"))
				Thread.sleep(1000);
			else
				Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		releaseLock(key);
		System.out.println("End(" + Thread.currentThread().getName() + ")");
	}

	private void redisLock(String key, int seconds) throws Exception {
		do {
			System.out.println("try-lock-(" + Thread.currentThread().getName() + ")");
			if (probeRedis.setNx(key, key) == 1) {
				probeRedis.expire(key, seconds);
				break;
			}
			bq.put(new BlockEle(Thread.currentThread(), key));
		} while (true);
	}

	private void releaseLock(String key) throws Exception {
		probeRedis.del(key);
		for (Iterator<BlockEle> iterator = bq.iterator(); iterator.hasNext();) {
			BlockEle blockEle = iterator.next();
			if (key.equals(blockEle.getKey())) {
				bq.remove(blockEle);
				break;
			}
		}
	}

}
