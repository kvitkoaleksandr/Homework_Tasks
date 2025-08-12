package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(Arrays.asList("John", "Paul", "Ringo", "George"));
        names.forEach(out::println);
        out.println(names);
    }
}