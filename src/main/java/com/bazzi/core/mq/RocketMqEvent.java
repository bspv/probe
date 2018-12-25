package com.bazzi.core.mq;

import lombok.Getter;
import lombok.Setter;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationEvent;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class RocketMqEvent extends ApplicationEvent {
	private static final long serialVersionUID = -4387112228668494524L;
	private MessageExt messageExt;
	private String topic;
	private String tag;
	private String body;

	public RocketMqEvent(MessageExt msg) {
		this(msg, StandardCharsets.UTF_8);
	}

	public RocketMqEvent(MessageExt msg, Charset charset)  {
		super(msg);
		this.messageExt = msg;
		this.topic = msg.getTopic();
		this.tag = msg.getTags();
		byte[] bytes = msg.getBody();
		this.body = bytes == null || bytes.length == 0 ? ""
				: new String(bytes, charset == null ? StandardCharsets.UTF_8 : charset);
	}

	public String toString() {
		return "RocketMQEvent [topic=" + topic + ", tag=" + tag + ", body=" + body + ", msgId=" + messageExt.getMsgId()
				+ ", msgTime=" + messageExt.getBornTimestamp() + "]";
	}

}
