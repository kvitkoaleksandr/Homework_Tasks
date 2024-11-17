package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSorExampleTest {

    @Test
    void testAlreadySortedArray() {
        BubbleSorExample sorter = new BubbleSorExample();
        int[] arr = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};

        sorter.bubbleSort(arr);

        assertArrayEquals(expected, arr, "Массив уже отсортирован, изменений быть не должно.");
    }

    @Test
    void testReverseSortedArray() {
        BubbleSorExample sorter = new BubbleSorExample();
        int[] arr = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};

        sorter.bubbleSort(arr);

        assertArrayEquals(expected, arr, "Массив, отсортированный в обратном порядке, должен быть правильно отсортирован.");
    }

    @Test
    void testUnsortedArray() {
        BubbleSorExample sorter = new BubbleSorExample();
        int[] arr = {3, 1, 4, 1, 5, 9, 2};
        int[] expected = {1, 1, 2, 3, 4, 5, 9};

        sorter.bubbleSort(arr);

        assertArrayEquals(expected, arr, "Несортированный массив должен быть правильно отсортирован.");
    }

    @Test
    void testEmptyArray() {
        BubbleSorExample sorter = new BubbleSorExample();
        int[] arr = {};
        int[] expected = {};

        sorter.bubbleSort(arr);

        assertArrayEquals(expected, arr, "Пустой массив должен остаться пустым.");
    }

    @Test
    void testSingleElementArray() {
        BubbleSorExample sorter = new BubbleSorExample();
        int[] arr = {42};
        int[] expected = {42};

        sorter.bubbleSort(arr);

        assertArrayEquals(expected, arr, "Массив из одного элемента должен остаться неизменным.");
    }

    @Test
    void testArrayWithDuplicates() {
        BubbleSorExample sorter = new BubbleSorExample();
        int[] arr = {4, 2, 4, 1, 3};
        int[] expected = {1, 2, 3, 4, 4};

        sorter.bubbleSort(arr);

        assertArrayEquals(expected, arr, "Массив с дубликатами должен быть правильно отсортирован.");
    }
}