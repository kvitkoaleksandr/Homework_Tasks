package org.example.home_tasks;

public class Reverse {
    public void show() {
        int[] array = {1, 2, 3, 4, 5};
        System.out.println("Оригинальный массив:");
        printArray(array);

        reverseArray(array);

        System.out.println("Перевёрнутый массив:");
        printArray(array);
    }

    public static void reverseArray(int[] array) {
        int left = 0;
        int right = array.length - 1;

        while (left < right) {
            // Обмен значениями
            int temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // Сдвигаем указатели
            left++;
            right--;
        }
    }

    public static void printArray(int[] array) {
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}