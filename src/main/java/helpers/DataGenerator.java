package helpers;

import java.util.Random;

public class DataGenerator {

    public static String generateRandomUsername(String prefix) {
        System.out.println("Generating a username");
        String username = prefix + get5RandomDigits();
        System.out.println(username);
        return username;
    }

    private static String get5RandomDigits() {
        System.out.println("Making a random number between 10000 and 99999");
        Random rand = new Random();
        Integer num = rand.nextInt((99999 - 10000) + 1) + 10000;
        System.out.println(num);
        return num.toString();
    }

}
