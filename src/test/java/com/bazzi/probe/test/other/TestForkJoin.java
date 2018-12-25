package com.bazzi.probe.test.other;

import com.bazzi.probe.test.TestBase;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class TestForkJoin extends TestBase {

	@Test
	public void testFork() {
		long[] arr = new long[199000];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = i + 1;
		}
		ForkJoinPool fjp = new ForkJoinPool(4); // 最大并发数4
		ForkJoinTask<Long> task = new SumTask(arr, 0, arr.length);
		long startTime = System.currentTimeMillis();
		Long result = fjp.invoke(task);
		long endTime = System.currentTimeMillis();
		print("Fork/join sum: " + result + " in " + (endTime - startTime) + " ms.");
		long startTime2 = System.currentTimeMillis();
		long sum = Arrays.stream(arr).sum();
		long endTime2 = System.currentTimeMillis();
		print(sum + ",time" + (endTime2 - startTime2));
	}

	//	@Test
	public void testStr() {
	}
}
