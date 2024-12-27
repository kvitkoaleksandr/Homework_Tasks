package org.example.interviews;

public class FizzBuzzTestInterview {

    public byte[] fizzBuzzTest(int value) {
        if (value % 3 == 0 && value % 5 == 0) {
            return "FizzBuzz".getBytes(); // Возвращает байты строки "FizzBuzz"
        } else if (value % 3 == 0) {
            return "Fizz".getBytes(); // Возвращает байты строки "Fizz"
        } else if (value % 5 == 0) {
            return "Buzz".getBytes(); // Возвращает байты строки "Buzz"
        } else {
            System.out.println("Конец");
            System.out.println(" RuntimeException Value does not meet any condition");
            //throw new RuntimeException("Value does not meet any condition");
        }

        return new byte[0];
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