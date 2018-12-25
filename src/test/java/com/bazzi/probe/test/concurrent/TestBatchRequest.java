package com.bazzi.probe.test.concurrent;

import com.bazzi.core.util.HttpUtil;
import com.bazzi.probe.test.TestBase;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestBatchRequest extends TestBase {

	private static final String LOGIN_URL = "http://192.168.0.45:9002/xxwht/user/login.html";
	private static final String QR_CODE_URL = "http://192.168.0.45:9002/xxwht/qrCode/qrCode.html";
	
	private static final String QR_CODE = "1YMUW1UFPORIKRUMOWWV9ASM_24";

	@Test
	public void testBatchRequest() throws Exception {
		int total = 20;
		CountDownLatch cdl = new CountDownLatch(1);
		CountDownLatch cdlAll = new CountDownLatch(total);
		ExecutorService es = Executors.newFixedThreadPool(total);
		for (int i = 0; i < total; i++) {
			final int idx = i;
			es.execute(new Runnable() {
				public void run() {
					try {
						String cookie = LoginHelper.sendLogin(LOGIN_URL, "1342633" + (1143 + idx));
						Map<String, String> header = Maps.newHashMap(getHeaderMap());
						header.put("Cookie", cookie);

						Map<String, String> qrCode = Maps.newHashMap();
						qrCode.put("context", QR_CODE);
						cdlAll.countDown();
						cdl.await();

						String result = HttpUtil.sendPost(QR_CODE_URL, qrCode, header);
						print(result);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		cdlAll.await();
		print("所有session信息获取成功，执行二维码激活步骤！");
		cdl.countDown();
		es.shutdown();
		Thread.sleep(20000);
	}

	private static Map<String, String> getHeaderMap() {
		Map<String, String> header = Maps.newHashMap();
		header.put("Content-Type", "application/json;charset=UTF-8");
		header.put("X-CHANNEL-ID", "Google Play");
		header.put("X-CLIENT-VERSION", "2.0.1");
		header.put("X-MACHINE-ID", "51d4ffb9c7b53b474adeca6d4b7f61f3");
		header.put("X-PLATFORM", "0");
		return header;
	}

}
