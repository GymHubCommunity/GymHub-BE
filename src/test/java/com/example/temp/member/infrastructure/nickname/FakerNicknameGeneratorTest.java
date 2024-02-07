package com.example.temp.member.infrastructure.nickname;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FakerNicknameGeneratorTest {

    FakerNicknameGenerator nicknameGenerator;

    @BeforeEach
    void setUp() {
        nicknameGenerator = new FakerNicknameGenerator();
    }

    @Test
    @DisplayName("랜덤한 닉네임을 생성한다")
    void createNickname() throws Exception {
        Set<String> nicknames = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            Nickname nickname = nicknameGenerator.generate();
            assertThat(nickname).isNotNull();
            nicknames.add(nickname.getNickname());
        }
        assertThat(nicknames.size()).isBetween(2, 1000);
    }

}