package com.example.temp.oauth.impl.kakao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KakaoTokenTest {

    @DisplayName("카카오 인증헤더에 'Bearer value' 형식으로 출력한다.")
    @Test
    void getAuthorizationValue() throws Exception {
        // given
        KakaoToken token = new KakaoToken("test_access_token", "Bearer");

        // when
        String result = token.getValueUsingAuthorizationHeader();

        // then
        assertThat(result).isEqualTo("Bearer test_access_token");
    }

}