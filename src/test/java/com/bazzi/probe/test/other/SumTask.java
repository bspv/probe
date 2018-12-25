package com.bazzi.probe.test.other;

import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {
	private static final long serialVersionUID = 3451902138497735135L;
	private static final int THRESHOLD = 22;
	private long[] arr;
	private int start;
	private int end;

	public SumTask(long[] ary, int start, int end) {
		if (ary != null && ary.length > 0) {
			this.arr = new long[ary.length];
			System.arraycopy(ary, 0, arr, 0, ary.length);
		}
		this.start = start;
		this.end = end;
	}

	protected Long compute() {
		if (end - start <= THRESHOLD) {
			long sum = 0;
			for (int i = start; i < end; i++) {
				sum += arr[i];
			}
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//			}
//			log.debug("compute {}~{} = {}", start, end, sum);
			return sum;
		}
		int a = (start + end) / 2;
		int b = (start + a) / 2;
		int c = (a + end) / 2;
//		log.debug("split {}~{}===>{}~{};{}~{};{}~{};{}~{}", start, end, start, b, b, a, a, c, c, end);
		SumTask sumTask1 = new SumTask(arr, start, b);
		SumTask sumTask2 = new SumTask(arr, b, a);
		SumTask sumTask3 = new SumTask(arr, a, c);
		SumTask sumTask4 = new SumTask(arr, c, end);
		sumTask1.fork();
		sumTask2.fork();
		sumTask3.fork();
		sumTask4.fork();
		Long result1 = sumTask1.join();
		Long result2 = sumTask2.join();
		Long result3 = sumTask3.join();
		Long result4 = sumTask4.join();
		Long result = result1 + result2 + result3 + result4;
//		log.debug("result = " + result1 + " + " + result2 + " + " + result3 + " + " + result4 + " ==> " + result);
		return result;
	}
}
