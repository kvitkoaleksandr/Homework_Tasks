package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SelectionSortTest {
    SelectionSort sorter = new SelectionSort();

    @Test
    void testSelectionSortEmpty() {
        int[] arr = {};
        int[] expected = {};

        sorter.selectionSort(arr);

        assertArrayEquals(arr, expected, "Пустой массив должен остаться пустым.");
    }

    @Test
    void testSelectionSort() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2};
        int[] expected = {1, 1, 2, 3, 4, 5, 9};

        sorter.selectionSort(arr);

        assertArrayEquals(expected, arr, "Несортированный массив должен быть правильно отсортирован.");
    }

    @Test
    void testSelectionSortFinish() {
        int[] one = {1, 2, 3, 4, 5};
        int[] two = {1, 2, 3, 4, 5};

        sorter.selectionSort(one);

        assertArrayEquals(one, two, "Массив уже отсортирован, изменений быть не должно.");
    }

    @Test
    void testSelectionSortReverseOrder() {
        int[] one = {5, 4, 3, 2, 1};
        int[] two = {1, 2, 3, 4, 5};

        sorter.selectionSort(one);

        assertArrayEquals(one, two, "Массив с обратным порядком.");
    }

    @Test
    void testSelectionSortRepetElements() {
        int[] arr = {4, 2, 4, 1, 3};
        int[] expected = {1, 2, 3, 4, 4};

        sorter.selectionSort(arr);

        assertArrayEquals(expected, arr, "Массив с дубликатами должен быть правильно отсортирован.");
    }

    @Test
    void testSelectionSortSingleElement() {
        int[] arr = {42};
        int[] expected = {42};

        sorter.selectionSort(arr);

        assertArrayEquals(expected, arr, "Массив из одного элемента должен остаться неизменным.");
    }

    @Test
    void testSelectionSortLargeArray() {
        int[] arr = new int[1000];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr.length - i; // Заполняем массив в обратном порядке
        }

        int[] expected = new int[1000];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = i + 1; // Заполняем массив по возрастанию
        }

        sorter.selectionSort(arr);

        assertArrayEquals(expected, arr, "Большой массив должен быть правильно отсортирован.");
    }
}