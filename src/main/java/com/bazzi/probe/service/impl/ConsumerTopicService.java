package com.bazzi.probe.service.impl;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bazzi.core.mq.RocketMqEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConsumerTopicService {
	/**
	 * 事件监听，topic和tag来处理符合的消息
	 * 
	 * @param event
	 */
	@EventListener(condition = "#event.topic==@propertyUtil.getProperty('mq.topic') && #event.tag==@propertyUtil.getProperty('mq.tag')")
//	@EventListener(condition = "#event.topic==T(com.youliaoar.demo.util.Const).TOPIC && #event.tag=='TagA'")
//	@EventListener(condition = "#event.topic=='TopicTest' && #event.tag=='TagA'")
	public void consumerForTopic(RocketMqEvent event) {
		log.info("ConsumerMsg:{}", event);
	}

}
