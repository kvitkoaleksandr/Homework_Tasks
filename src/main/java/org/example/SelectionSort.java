package org.example;

public class SelectionSort {
    public void selectionSort(int[] arr) {
        int swapCount = 0;

        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                int temp = arr[minIndex];
                arr[minIndex] = arr[i];
                arr[i] = temp;
                swapCount++;
            }
        }
        System.out.println("\nКоличество обменов: " + swapCount);
    }

    public void mainNew() {
        int[] arr = {64, 25, 12, 22, 11};

        System.out.println("\nМассив до сортировки: ");
        printArray(arr);

        selectionSort(arr);

        System.out.println("\nМассив после сортировки: ");
        printArray(arr);
    }

    private void printArray(int[] arr) {
        for(int num: arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}