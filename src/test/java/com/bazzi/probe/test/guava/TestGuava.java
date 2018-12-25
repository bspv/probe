package com.bazzi.probe.test.guava;

import com.bazzi.probe.test.TestBase;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGuava extends TestBase {

	@Test
	public void testCountBySet() {
		HashMultiset<String> set = HashMultiset.create();
		String[] arr = new String[]{"a", "b", "a", "c", "d", "c", "a", "a"};
		set.addAll(Arrays.asList(arr));

		int c = set.count("a");
		print(c);
	}

	@Test
	public void testCollection() {
		List<String> list = Lists.newArrayList();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		Integer idList[] = new Integer[]{1, 3, 5, 7};
		Joiner joiner = Joiner.on(",").skipNulls();
		String res = joiner.join(idList);
		print(res);

		String[] arr = ",a,,b,".split(",");
		print(arr.length);

		Map<String, String> map = new HashMap<String, String>() {
			private static final long serialVersionUID = 6923696176351137159L;

			{
				put("success", "true");
			}
		};

		Map<String, String> mapN = Maps.newHashMap(map);
		mapN.put("fail", "false");
		mapN.forEach((key, val) -> print(key + "," + val));
//		for (Map.Entry<String, String> entry : mapN.entrySet()) {
//			print(entry.getKey() + "," + entry.getValue());
//		}
	}

}
