package com.example.temp.common.utils.random;

import java.util.Random;
import org.springframework.stereotype.Component;

/**
 * 랜덤한 난수를 생성합니다. 생성되는 난수는 [알파벳 대소문자, 숫자] 로 구성되어 있습니다. 기본으로 생성되는 난수의 길이는 20입니다.
 *
 * @warning 보안에 관련한 용도로는 사용해서는 안됩니다. 해당 생성기로 만들어지는 난수는 예측이 가능합니다.
 */
@Component
@SuppressWarnings("java:S2245")
public class DefaultRandomGenerator implements RandomGenerator {

    public static final int DEFAULT_SIZE = 20;
    private static final String DEFAULT_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    private final Random random = new Random("GymHub".hashCode());

    /**
     * @param length
     * @return 길이가 length인 난수
     */
    @Override
    public String generate(int length) {
        return generateHelper(length, this.random);
    }

    /**
     * 길이가 DEFAULT인 난수를 반환합니다.
     */
    @Override
    public String generate() {
        return generate(DEFAULT_SIZE);
    }

    /**
     * 길이가 length인 고정된 난수를 반환합니다.
     *
     * @param seed
     * @param length
     */
    @Override
    public String generateWithSeed(String seed, int length) {
        Random randomGenerator = new Random(seed.hashCode());
        return generateHelper(length, randomGenerator);
    }


    private String generateHelper(int length, Random random) {
        StringBuilder randomStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(DEFAULT_CHARSET.length());
            char ch = DEFAULT_CHARSET.charAt(number);
            randomStr.append(ch);
        }
        return randomStr.toString();
    }
}
