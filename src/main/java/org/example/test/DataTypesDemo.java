package org.example.test;

public class DataTypesDemo {
    byte b = 1;
    short s = 2;
    int i = 3;
    long l = 4L;
    float f = 4.2f;
    double d = 3;
    char c = 'd';
    boolean boo = true;

    int[] ints = {1, 2, 3, 4, 5};
    String[] strings = {"s", "t", "r", "i", "n", "g"};

    public void show() {
        System.out.print(b);
        System.out.print(s);
        System.out.print(i);
        System.out.print(l);
        System.out.print(f);
        System.out.print(d);
        System.out.print(c);
        System.out.print(boo);
        System.out.println();

        for(int i: ints) {
            System.out.println(i);
        }
        for(String s: strings) {
            System.out.println(s);
        }
    }
}