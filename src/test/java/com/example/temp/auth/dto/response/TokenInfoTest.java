package com.example.temp.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.auth.dto.response.TokenInfo.TokenInfoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenInfoTest {

    String accessToken;
    String refreshToken;

    @BeforeEach
    void setUp() {
        accessToken = "엑세스";
        refreshToken = "리프레쉬";
    }

    @Test
    @DisplayName("빌더를 사용해 TokenInfo를 생성한다")
    void createTokenUsingBuilder() {
        TokenInfo tokenInfo = TokenInfo.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();

        assertThat(tokenInfo.accessToken()).isEqualTo(accessToken);
        assertThat(tokenInfo.refreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("TokenInfo에는 access token이 필수로 들어가야 한다.")
    void validateTokenInfoAboutNoAccessToken() {
        assertThatThrownBy(() -> createTokenInfo(null, refreshToken))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new TokenInfo(null, refreshToken))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("TokenInfo에는 refresh token이 필수로 들어가야 한다.")
    void validateTokenInfoAboutNoRefreshToken() {
        assertThatThrownBy(() -> createTokenInfo(accessToken, null))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new TokenInfo(accessToken, null))
            .isInstanceOf(NullPointerException.class);
    }

    private TokenInfo createTokenInfo(String accessToken, String refreshToken) {
        TokenInfoBuilder builder = TokenInfo.builder();
        if (accessToken != null) {
            builder.accessToken(accessToken);
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken);
        }
        return builder.build();
    }

}