package com.example.temp.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthorizedUrlTest {

    @Test
    @DisplayName("생성자의 순서가 올바른지 테스트한다")
    void create() throws Exception {
        // given
        String authorizedUrl = "authorizedUrl";

        // when
        AuthorizedUrl result = new AuthorizedUrl(authorizedUrl);

        // then
        assertThat(result.authorizedUrl()).isEqualTo(authorizedUrl);
    }
}