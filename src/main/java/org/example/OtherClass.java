package org.example;

public class OtherClass {
    AccessModifiersDemo test = new AccessModifiersDemo();
    public void show() {
        System.out.println(test.defaultField);
        System.out.println(test.protectedField);
        System.out.println(test.publicField);
        test.getPrivateField();
        test.showAll();
    }
}
