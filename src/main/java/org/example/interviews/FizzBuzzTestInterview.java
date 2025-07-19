package org.example.interviews;

public class FizzBuzzTestInterview {

    public byte[] fizzBuzzTest(int value) {
        String result = "";
        if (value % 3 == 0) {
            result += "Fizz";
        }
        if (value % 5 == 0) {
            result += "Buzz";
        }
        if (result.length() == 0) {
            System.out.println("Конец");

            //throw new IllegalArgumentException("Value does not meet any condition");
        }

        return result.getBytes();
    }

    public void showTest() {
        FizzBuzzTestInterview test = new FizzBuzzTestInterview();
        // Тестируем число 15:
        byte[] result = test.fizzBuzzTest(15); // Должно вернуть "FizzBuzz" в виде байтов
        System.out.println(new String(result)); // Преобразуем байты обратно в строку

        // Тестируем число 3:
        result = test.fizzBuzzTest(3); // Должно вернуть "Fizz"
        System.out.println(new String(result));

        // Тестируем число 5:
        result = test.fizzBuzzTest(5); // Должно вернуть "Buzz"
        System.out.println(new String(result));

        // Тестируем число, не кратное 3 или 5:
        test.fizzBuzzTest(7); //IllegalArgumentException
    }
}