package com.example.temp.auth.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthLoginRequestTest {

    @Test
    @DisplayName("생성자의 순서가 올바른지 테스트한다")
    void create() throws Exception {
        // given
        String authCode = "authCode";

        // when
        OAuthLoginRequest result = new OAuthLoginRequest(authCode);

        // then
        assertThat(result.authCode()).isEqualTo(authCode);
    }

}