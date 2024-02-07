package com.example.temp.member.infrastructure.nickname;

import static com.example.temp.member.infrastructure.nickname.Nickname.NICKNAME_MAX_LENGTH;

import java.util.Random;
import org.springframework.stereotype.Component;

/**
 * 알파벳 대소문자와 숫자를 포함하여 NICKNAME_MAX_LENGTH 길이만큼의 랜덤한 문자열을 생성하는 객체입니다.
 */
@Component
public class RandomNicknameGenerator implements NicknameGenerator {

    private static final String ALPHABETS_AND_NUM_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    private static final char[] ALPHABETS_AND_NUM = createAlphabetsAndNums();

    private static char[] createAlphabetsAndNums() {
        return ALPHABETS_AND_NUM_STR.toCharArray();
    }

    private final Random random = new Random();

    @Override
    public Nickname generate() {
        StringBuilder sb = new StringBuilder(NICKNAME_MAX_LENGTH);
        for (int i = 0; i < NICKNAME_MAX_LENGTH; i++) {
            sb.append(getRandomCharacter());
        }
        return Nickname.create(sb.toString());
    }

    private char getRandomCharacter() {
        return ALPHABETS_AND_NUM[random.nextInt(ALPHABETS_AND_NUM.length)];
    }
}