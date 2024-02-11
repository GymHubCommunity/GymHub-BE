package com.example.temp.common.utils.random;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class DefaultRandomGenerator implements RandomGenerator {

    public static final int DEFAULT_SIZE = 10;
    private static final String DEFAULT_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    private final Random random = new Random(12312);

    @Override
    public String generate(int size) {
        StringBuilder randomStr = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int number = this.random.nextInt(DEFAULT_CHARSET.length());
            char ch = DEFAULT_CHARSET.charAt(number);
            randomStr.append(ch);
        }
        return randomStr.toString();
    }

    @Override
    public String generate() {
        return generate(DEFAULT_SIZE);
    }

    @Override
    public String generateWithSeed(String seed, int length) {
        long seedLong = seed.hashCode();
        Random randomGenerator = new Random(seedLong);
        StringBuilder randomStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = randomGenerator.nextInt(DEFAULT_CHARSET.length());
            char ch = DEFAULT_CHARSET.charAt(number);
            randomStr.append(ch);
        }
        return randomStr.toString();
    }

}
