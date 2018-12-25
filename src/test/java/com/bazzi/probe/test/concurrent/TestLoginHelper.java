package com.bazzi.probe.test.concurrent;

import com.bazzi.core.util.HttpUtil;
import com.bazzi.probe.test.TestBase;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

public class TestLoginHelper extends TestBase {
	// 1YMUW1UFPORIKRUMOWWV9ASM_24
	// 8K6LPH9DDVXKDCPMRFWZ7AGF_24
	// TSKHSZIJRMPKRYG0MCWZKBF8_24
	// 8EUY6NBXRUZ65V3PN1SBQDVT_24
	// EAP8LOTOJGN2F2OHKAW7KUWG_24

	@Test
	public void testHttpClientUtil() {
		String cookie = LoginHelper.sendLogin("http://192.168.0.45:9002/xxwht/user/login.html", "13426331143");

		Map<String, String> header = Maps.newHashMap(getHeaderMap());
		header.put("Cookie", cookie);

		Map<String, String> qrCode = Maps.newHashMap();
		qrCode.put("context", "1YMUW1UFPORIKRUMOWWV9ASM_24");

		String result = HttpUtil.sendPost("http://192.168.0.45:9002/xxwht/qrCode/qrCode.html", qrCode, header);
		print(result);
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
