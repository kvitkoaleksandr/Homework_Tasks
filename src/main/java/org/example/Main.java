package org.example;

public class Main {
    public static void main(String[] args) {
        DataTypesDemo test = new DataTypesDemo();
        VariableDemo testToo = new VariableDemo();
        MethodDemo testThree = new MethodDemo();
        OtherClass testFo = new OtherClass();

        test.show();
        System.out.println();
        testToo.showVariable();
        System.out.println();
        testThree.sum(1, 2);
        System.out.println();
        testThree.sum(3, 4);
        System.out.println();
        testFo.show();


    }
}