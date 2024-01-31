package com.example.temp.auth.oauth.impl.google;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoogleTokenTest {

    @Test
    @DisplayName("인증헤더에 사용할 Value를 만든다")
    void getAuthorizationValue() throws Exception {
        // given
        GoogleToken token = new GoogleToken("access", "Bearer");

        // when
        String result = token.getValueUsingAuthorizationHeader();

        // then
        assertThat(result).isEqualTo("Bearer access");
    }

}