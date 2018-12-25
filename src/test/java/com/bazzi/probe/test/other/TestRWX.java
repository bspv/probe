package com.bazzi.probe.test.other;

import org.junit.Test;

import com.bazzi.probe.test.TestBase;

public class TestRWX extends TestBase {

	@Test
	public void testRwx() {
		for (int i = 1; i <= 7; i++) {
			String r = (i >> (3 - 1) & 1) == 1 ? "r" : "-";
			String w = (i >> (2 - 1) & 1) == 1 ? "w" : "-";
			String x = (i >> (1 - 1) & 1) == 1 ? "x" : "-";
			print(r + w + x);
		}
	}

}
