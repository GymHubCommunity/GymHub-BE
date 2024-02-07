package com.example.temp.member.infrastructure.nickname;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NicknameTest {

    @Test
    @DisplayName("Nickname의 값이 일치하면 동등한 객체인지 확인한다.")
    void equals() throws Exception {
        // given
        Nickname nickname1 = Nickname.create("nic");
        Nickname nickname2 = Nickname.create("nic");

        // when & then
        assertThat(nickname1.equals(nickname2)).isTrue();
    }

    @Test
    @DisplayName("Nickname의 값이 일치하면 해시코드가 일치하는지 확인한다.")
    void hashcode() throws Exception {
        // given
        Nickname nickname1 = Nickname.create("nic");
        Nickname nickname2 = Nickname.create("nic");

        // when
        boolean result = nickname1.hashCode() == nickname2.hashCode();

        // then
        assertThat(result).isTrue();
    }

}