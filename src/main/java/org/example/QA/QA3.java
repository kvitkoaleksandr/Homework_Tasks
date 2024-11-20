package org.example.QA;

public class QA3 {
    // Основной метод быстрой сортировки
    public void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // Разделяем массив и находим позицию pivot
            int pivotIndex = partition(arr, low, high);

            // Рекурсивно сортируем левую часть массива
            quickSort(arr, low, pivotIndex - 1);

            // Рекурсивно сортируем правую часть массива
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    // Метод для разделения массива
    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; // Берём последний элемент как pivot
        int i = low - 1; // для элемента, который должен быть меньше pivot

        for (int j = low; j < high; j++) {
            // Если текущий элемент меньше pivot, перемещаем его влево
            if (arr[j] < pivot) {
                i++;
                // Меняем местами arr[i] и arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Меняем местами pivot и элемент arr[i + 1], чтобы pivot встал на своё место
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1; // Возвращаем индекс pivot
    }

    public void mainNew() {
        int[] arr = {10, 80, 30, 90, 40, 50, 70};

        System.out.println("\nМассив до сортировки: ");
        printArray(arr);

        quickSort(arr, 0, arr.length - 1); // Вызываем сортировку

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