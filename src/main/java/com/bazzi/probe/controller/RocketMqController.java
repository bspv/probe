package com.bazzi.probe.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bazzi.core.result.Result;
import com.bazzi.core.util.PropertyUtil;

//@Slf4j
@Controller
public class RocketMqController {

//	@Resource
//	private Producer producer;

	@Resource
	private PropertyUtil propertyUtil;

	@RequestMapping(value = "/msgIdx", method = {RequestMethod.POST, RequestMethod.GET})
	public String idx() {
		return "mq/msgIdx";
	}

	@ResponseBody
	@RequestMapping(value = "/sendMsg", method = {RequestMethod.POST, RequestMethod.GET})
	public Result<String> sendMsg(@RequestParam Integer idx, @RequestParam Integer pageSize,
								  @RequestParam String current) {
//		Result<String> result = new Result<>();
//		SendResult sr =
//				producer.producerSend(propertyUtil.getProperty("mq.topic"),
//						propertyUtil.getProperty("mq.tag"), "idx:" + idx + ",pageSize:" +
//								pageSize + ",current:" + current);
//		SendResult sr = producer.producerSend(propertyUtil.getProperty("mq.topic"), propertyUtil.getProperty("mq.tag"),
//				new Ver(idx, pageSize, current));
//		result.setData(JSON.toJSONString(sr));
//		log.debug("idx:{},size:{},cur:{},result:{}", idx, pageSize, current, result.getData());
//		return result;
		return null;
	}

}
