package com.bazzi.core.mq;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
public class Consumer {
	private static final int BATCH_SIZE = 1;// 批量
	private static final int RECONSUME_TIMES = 3;// 重发次数
	private static final int DELAY_TIME = 5;// 启动延迟

	private String namesrvAddr;
	private String consumerGroupName;
	private String topic;

	@Resource
	private ApplicationEventPublisher publisher;

	private static DefaultMQPushConsumer consumer;

	private static void setPushConsumer(DefaultMQPushConsumer defaultMQPushConsumer){

	}

	/**
	 * 初始化消费者
	 * 
	 * @throws Exception 异常
	 */
	public synchronized void init() throws Exception {
		// 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例
		// 注意：ConsumerGroupName需要由应用来保证唯一
//		consumer = new DefaultMQPushConsumer(consumerGroupName);
		setPushConsumer(new DefaultMQPushConsumer(consumerGroupName));
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.setNamesrvAddr(namesrvAddr);
		// 设置批量消费，以提升消费吞吐量，默认是1
		consumer.setConsumeMessageBatchMaxSize(BATCH_SIZE);

		// 订阅指定topic下tags
		consumer.subscribe(topic, "*");

		consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
			for (MessageExt msg : msgs) {
				try {
					publisher.publishEvent(new RocketMqEvent(msg));
				} catch (Exception e) {
					if (msg.getReconsumeTimes() < RECONSUME_TIMES) {// 重复消费3次
						log.info("consumeMessage RECONSUME_LATER--->MSG:{}", msg);
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					} else {
						log.error("consumeMessage FAIL--->MSG:{}", msg);
					}
				}
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		// 延迟N秒再启动，主要是等待spring事件监听相关程序初始化完成，否则会出现对RocketMQ的消息进行消费后立即发布消息到达的事件，然而此事件的监听程序还未初始化，从而造成消息的丢失
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.schedule(() -> {
			try {
				// Consumer对象在使用之前必须要调用start初始化，初始化一次即可
				consumer.start();
				log.info("Consumer Has Started!");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				log.info("Consumer Start Failure! Cause:{}", e.getMessage());
			}
		}, DELAY_TIME, TimeUnit.SECONDS);
		ses.shutdown();
	}

	public synchronized void destroy() {
		consumer.shutdown();
		log.info("Consumer Has been Shutdown!");
	}

}
