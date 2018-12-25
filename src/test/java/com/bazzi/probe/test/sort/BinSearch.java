package com.bazzi.probe.test.sort;

public class BinSearch {
	public static void main(String[] args) {
		int[] arr = new int[] { 1,2,3,4,5,6,8 };
		int i = binSearch(arr, 5);
		System.out.println(i);
	}

	public static int binSearch(int[] arr, int key) {
		int left = 0, right = arr.length - 1;
		while (left <= right) {
			int mid = (left + right) >>> 1;
			if (arr[mid] == key) {
				return mid;
			} else if (arr[mid] < key) {
				left = mid + 1;
			} else {
				right = mid - 1;
			}
		}
		return -1;
	}

}
