package com.bazzi.probe.test.sort;

public class InsertSort {
	public static void main(String[] args) {
		int[] arr = new int[] { 4, 7, 2, 3, 1 };
		insert(arr);
		for (int i : arr) {
			System.out.println(i);
		}
	}

	private static void insert(int[] arr) {
		for (int i = 1; i < arr.length; i++) {
			int temp = arr[i];
			int j;
			for (j = i - 1; j >= 0 && temp < arr[j]; j--) {
				arr[j + 1] = arr[j];
			}
			arr[j + 1] = temp;
		}
	}

}
