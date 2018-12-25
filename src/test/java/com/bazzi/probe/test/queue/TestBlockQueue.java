package com.bazzi.probe.test.queue;

import com.bazzi.probe.queue.BlockQueue;
import org.junit.Test;

import com.bazzi.probe.test.TestBase;

public class TestBlockQueue extends TestBase {
	
	@Test
	public void testQueue() throws InterruptedException {
		final BlockQueue bq = new BlockQueue();
		new Thread(new Runnable() {
			public void run() {
				try {
					for (int i = 0; i < 15; i++) {
						print("put(" + i + ")");
						bq.put(i);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {

			public void run() {
				try {
					for (int i = 0; i < 12; i++) {
						print("take---(" + bq.take() + ")");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		Thread.sleep(5000);
	}

}
