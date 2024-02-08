package com.example.temp.member.infrastructure.nickname;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RandomNicknameGeneratorTest {

    RandomNicknameGenerator nicknameGenerator;

    @BeforeEach
    void setUp() {
        nicknameGenerator = new RandomNicknameGenerator();
    }

    @Test
    @DisplayName("랜덤한 닉네임을 생성한다")
    void createNickname() throws Exception {
        Set<Nickname> nicknames = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            Nickname nickname = nicknameGenerator.generate();
            assertThat(nickname).isNotNull();
            nicknames.add(nickname);
        }
        assertThat(nicknames.size()).isBetween(2, 1000);
    }
}