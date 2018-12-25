package com.bazzi.probe.test;

import org.junit.runner.RunWith;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class) // 使用junit4进行测试
@ContextConfiguration({ "classpath:test_deploy.xml" }) // 加载配置文件
@Slf4j
public class TestBase {

	public void print(Object obj) {
		print(obj, true);
	}

	public void print(Object obj, boolean pretty) {
		GsonBuilder gb = GsonBuilderUtils.gsonBuilderWithBase64EncodedByteArrays();
		if (pretty)
			gb.setPrettyPrinting();
		gb.setDateFormat("yyyy-MM-dd HH:mm:ss");
		if (isJson(obj)) // 如果已经是JSON字符串了，那么只进行格式化
			obj = new JsonParser().parse(obj.toString());
		log.info(gb.create().toJson(obj));
	}

	/**
	 * 利用FASTJSON判断是不是JSON字符串
	 * 
	 * @param obj
	 * @return
	 */
	private static boolean isJson(Object obj) {
		try {
			if (obj != null) {
				JSON.parse(obj.toString());
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

}
