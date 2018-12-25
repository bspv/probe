package com.bazzi.core.mq;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
@Setter
public class Producer {
	private String producerGroupName;
	private String namesrvAddr;

	private static DefaultMQProducer producer;

	private static void setMqProducer(DefaultMQProducer mqProducer){
		producer = mqProducer;
	}

	/**
	 * 初始化生产者
	 * 
	 * @throws Exception 异常
	 */
	public synchronized void init() {
		try {
//			producer = new DefaultMQProducer(producerGroupName);
			setMqProducer(new DefaultMQProducer(producerGroupName));
			producer.setNamesrvAddr(namesrvAddr);
			producer.start();
			log.info("Producer Has Started! NamesrvAddr:{}", namesrvAddr);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.info("Producer Start Failure! Cause:{}", e.getMessage());
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param topic 话题
	 * @param tag 标签
	 * @param data 数据
	 * @return 发送结果
	 * @throws Exception 异常
	 */
	public <T extends Serializable> SendResult producerSend(String topic, String tag, T data) {
		return producerSend(topic, tag, JSON.toJSONString(data));
	}

	public SendResult producerSend(String topic, String tag, String data) {
		try {
			if (data == null)
				return null;
			Message msg = new Message(topic, // topic
					tag, // tag
					data.getBytes(StandardCharsets.UTF_8)// body
			);
			SendResult sendResult = producer.send(msg);
			log.info("producerSend ---> {},{}", msg, sendResult);
			return sendResult;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public synchronized void destroy() {
		producer.shutdown();
		log.info("Producer Has been Shutdown!");
	}

}
