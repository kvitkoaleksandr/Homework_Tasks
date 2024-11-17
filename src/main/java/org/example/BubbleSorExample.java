package org.example;

public class BubbleSorExample {
    public  void bubbleSort(int[] arr) {
        int temp;
        int iterations = 0;
        boolean swapped;
        for (int i = 0; i < arr.length - 1; i++) {
            swapped = false;
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j +1];
                    arr[j +1] = temp;
                    swapped = true;

                }
            }
            ++iterations ;
            if (!swapped) {
                break;
            }
        }
        System.out.print("\nВсего итераций было " + iterations );
    }

    public  void newMain() {
        int[] arr = {2, 7, 1, 5, 4, 6, 3};

        System.out.println("Массив до сортировки:");
        for (int num : arr) {
            System.out.print(num + " ");
        }

        bubbleSort(arr); // Вызываем метод сортировки

        System.out.println("\nМассив после сортировки:");
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }
}