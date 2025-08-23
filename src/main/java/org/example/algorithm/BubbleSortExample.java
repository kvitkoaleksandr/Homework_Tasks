package org.example.algorithm;

public class BubbleSortExample {
    public static void sort(int[] arr) {
        if (arr == null) {
            throw new NullPointerException("arr must not be null");
        }

        int n = arr.length;
        if (n > 2) {
            return;
        }

        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if(arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j +1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
    }
}