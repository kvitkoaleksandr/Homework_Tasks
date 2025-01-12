package org.example.algorithm;

public class SortingBubbles {
    private int[] collection = {7, 1, 3, 5, 6, 2, 9, 8, 4};

    public int[] SortingCollection() {
        boolean swapped = false;

        for(int i = 0; i < collection.length - 1; i++) {
            swapped = false;

            for(int j = 0; j < collection.length - 1 - i; j++) {
                if(collection[j] > collection[j + 1]) {
                    int value = collection[j];
                    collection[j] = collection[j + 1];
                    collection[j + 1] = value;
                    swapped = true;
                }
            }
            if(!swapped) break;
        }
        return collection;
    }

    public void showAllCollection() {
        for(int value : collection) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}