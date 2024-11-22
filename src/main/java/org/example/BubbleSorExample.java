package org.example;

public class BubbleSorExample {
    public  void bubbleSort(int[] arr) {
        int n = arr.length;
        boolean swapped; // Флаг, чтобы отслеживать, были ли замены на текущей итерации

        for (int i = 0; i < n - 1; i++) {
            swapped = false; // изначально предполагаем, что замен не будет

            // Внутренний цикл проходит по массиву
            for (int j = 0; j < n - i - 1; j++) {
                // Если элементы расположены неправильно, меняем их местами
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true; // Если произошла замена, устанавливаем флаг
                }
            }

            // Если на текущей итерации не было замен, массив уже отсортирован
            if (!swapped) break;
        }
    }

    public  void newMain() {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};

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