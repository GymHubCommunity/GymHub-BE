package com.example.temp.member.infrastructure.nickname;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NicknameTest {

    @Test
    @DisplayName("Nickname의 값이 일치하면 동등한 객체인지 확인한다.")
    void equals() throws Exception {
        // given
        Nickname nickname1 = Nickname.create("닉넴");
        Nickname nickname2 = Nickname.create("닉넴");

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

    @ParameterizedTest
    @DisplayName("Nickname을 생성한다.")
    @ValueSource(strings = {"닉넴NickNAME12", "닉넴", "ab", "12", "123456789012", "NiCKNAM2"})
    void createNickname(String str) throws Exception {
        // when & then
        assertDoesNotThrow(() -> Nickname.create(str));
    }


    @Test
    @DisplayName("Nickname의 길이는 12글자를 초과할 수 없다.")
    void nicknameTooLong() throws Exception {
        // when & then
        assertThatThrownBy(() -> Nickname.create("닉넴NickNAME123"))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.NICKNAME_TOO_LONG.getMessage());
    }

    @Test
    @DisplayName("Nickname의 길이는 2글자 이상이어야 한다.")
    void nicknameTooShort() throws Exception {
        // when & then
        assertThatThrownBy(() -> Nickname.create("닉"))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.NICKNAME_TOO_SHORT.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Nickname에는 모든 종류의 특수문자를 포함할 수 없다")
    @ValueSource(strings = {"!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "=", "{", "}", "[", "]",
        ":", ";", "'", "<", ">", "?", "/", ",", ".", "~", "`", "|", "\\", "\""})
    void nicknameTooLong(String specialChar) throws Exception {
        // when & then
        assertThatThrownBy(() -> Nickname.create("닉넴Nick1" + specialChar))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.NICKNAME_PATTERN_MISMATCH.getMessage());
    }

}