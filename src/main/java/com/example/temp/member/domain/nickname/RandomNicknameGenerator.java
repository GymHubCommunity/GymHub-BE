package com.example.temp.member.domain.nickname;

import com.example.temp.common.utils.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * NICKNAME_MAX_LENGTH 길이만큼의 랜덤한 문자열을 생성하는 객체입니다.
 */
@Component
@RequiredArgsConstructor
public class RandomNicknameGenerator implements NicknameGenerator {

    private final RandomGenerator randomGenerator;

    @Override
    public Nickname generate() {
        String randomValue = randomGenerator.generate(Nickname.NICKNAME_MAX_LENGTH);
        return Nickname.create(randomValue);
    }
}
