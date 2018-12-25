package com.bazzi.probe.test.concurrent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginHelper {
	public static String sendLogin(String url, String mobile) {
		if (url == null || "".equals(url))
			return null;
		Map<String, String> param = Maps.newHashMap();
		param.put("mobile", mobile);
		param.put("smsCode", "782316");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String cookie = null;
		try {
			HttpPost postReq = new HttpPost(url);
			StringEntity entity = new StringEntity(JSON.toJSONString(param), Charset.defaultCharset());
			postReq.setEntity(entity);
			response = httpClient.execute(postReq);
			Header[] headers = response.getAllHeaders();
			for (Header h : headers) {
				log.debug(h.getName() + "," + h.getValue());
				if ("Set-Cookie".equals(h.getName()))
					cookie = h.getValue().substring(0, h.getValue().indexOf(";"));
			}
			return cookie;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			if (httpClient != null)
				try {
					httpClient.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
		}
	}

}
