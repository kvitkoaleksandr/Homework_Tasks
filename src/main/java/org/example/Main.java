package org.example;
import org.example.interviews.FizzBuzzTestInterview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static java.util.stream.Collectors.toList;

public class Main {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(Arrays.asList("John", "Paul", "Ringo", "George"));
        names.forEach(System.out::println);
        System.out.println(names);
    }
}
/*
List<String> names = new ArrayList<>(Arrays.asList("John", "Paul", "Ringo", "George"));
        names.forEach(name -> names.add("The Beatles"));
        System.out.println(names);

String string = "Hello";

        switch(string) {
            case "hello":
                System.out.println("hello tiny world");
            default:
                System.out.println("Good bye");
                break;
            case "Hello":
                System.out.println("Hello Big World");
        }
        int[] array1 = {1, 2, 3, 4};
        int[][] array2 = new int[][] {{1, 2, 3, 4}, {5, 6, 7, 8}};
        int[][] array3 = {{1, 2}, {}};
        int[] array4 = {};

        System.out.println(array1.length); // 4
        System.out.println(array2.length); // 2
        System.out.println(array3.length); //2
        System.out.println(array4.length);//0

int value = 4;
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

         list.stream()
                .map(d -> d = value).forEach(out::println);

        out.println(list);


        */

