package com.bazzi.probe.test.mq;

import com.bazzi.core.mq.Producer;
import com.bazzi.core.util.PropertyUtil;
import com.bazzi.probe.test.TestBase;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestRocketmq extends TestBase {

	@Resource
	private Producer producer;
	@Resource
	private PropertyUtil propertyUtil;

	// @Test
	public void testSendMsg() throws Exception {
		for (int i = 0; i < 1; i++) {
			SendResult sr = producer.producerSend(propertyUtil.getProperty("mq.topic"),
					propertyUtil.getProperty("mq.tag"), "Hello RocketMQ Msg " + new Random().nextInt(100));
			print(sr);
		}
	}

	@Test
	public void testBatchSendMsg() throws Exception {
		int count = 10;
		ExecutorService es = Executors.newFixedThreadPool(16);
		CountDownLatch cdl = new CountDownLatch(count);
		long l = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			final Integer idx = i;
			es.execute(new Runnable() {
				public void run() {
					try {
						producer.producerSend(propertyUtil.getProperty("mq.topic"), propertyUtil.getProperty("mq.tag"),
								"Hello Batch RocketMQ Msg " + idx);
						cdl.countDown();
					} catch (Exception e) {
						print(e.getMessage());
					}
				}
			});
		}
		cdl.await();
		print("testBatchSendMsg use time:" + (System.currentTimeMillis() - l));
	}

	@Test
	public void testMsg() throws Exception {
		Thread.sleep(200000);
	}

}
