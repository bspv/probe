package com.bazzi.probe.test.other;

import java.util.HashMap;
import java.util.Map;

public class TestMap {
	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<>();
		map.put("1", 1);
		map.put("2", 2);
		map.put("3", 3);
		map.put("4", 4);
		map.put("5", 5);
		map.put("6", 6);
		map.put("7", 7);
		map.put("8", 8);
		map.put("9", 9);
		map.put("10", 10);
		map.put("11", 11);
		map.put("12", 12);
		map.put("13", 13);
		Integer i = map.get("2");
		System.out.println("map.get(\"2\") = " + i);

		int j = 64;
		int result = j << 3;
		System.out.println("result = " + result);
		map.forEach((k, v) -> System.out.println("key = " + k));
	}
}
