package org.example;
import org.example.interviews.FizzBuzzTestInterview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static java.lang.System.out;
import static java.util.stream.Collectors.toList;

public class Main {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(Arrays.asList("John", "Paul", "Ringo", "George"));
        names.forEach(out::println);
        out.println(names);

        List<String> name = new ArrayList<>(Arrays.asList("John", "Paul", "Ringo", "George"));
        names.forEach(n -> names.add("The Beatles"));
        out.println(names);

        String string = "Hello";

        switch(string) {
            case "hello":
                out.println("hello tiny world");
            default:
                out.println("Good bye");
                break;
            case "Hello":
                out.println("Hello Big World");
        }
        int[] array1 = {1, 2, 3, 4};
        int[][] array2 = new int[][] {{1, 2, 3, 4}, {5, 6, 7, 8}};
        int[][] array3 = {{1, 2}, {}};
        int[] array4 = {};

        out.println(array1.length); // 4
        out.println(array2.length); // 2
        out.println(array3.length); //2
        out.println(array4.length);//0

        int value = 4;
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        list.stream()

                .map(d -> d = value).forEach(out::println);

        out.println(list);
    }
}