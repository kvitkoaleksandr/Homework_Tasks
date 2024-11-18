package org.example;

public class AccessModifiersDemo {
    public int publicField = 1;
    private int privateField = 2;
    protected int protectedField = 3;
    int defaultField = 4;

    public void getPrivateField(){
        System.out.println(this.privateField);
    }

    public void showAll(){
        System.out.println(publicField);
        System.out.println(protectedField);
        System.out.println(defaultField);
    }
}
