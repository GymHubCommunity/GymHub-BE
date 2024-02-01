package com.example.temp.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.temp.auth.dto.response.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtTokenManagerTest {

    JwtTokenManager jwtTokenManager;

    @Mock
    Clock clock;

    @Mock
    JwtProperties jwtProperties;

    Long memberId;

    Instant standardInstant;

    String secretKey;

    JwtParser parser;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        standardInstant = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        secretKey = "isTestSecretisTestSecretisTestSecretisTestSecretisTestSecret";

        parser = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
            .build();
    }

    @Test
    @DisplayName("access Token과 refresh Token을 생성한다.")
    void createTokenInfo() throws Exception {
        // given
        mockingJwtProperties(secretKey, 1800L, 10000L);
        mockingClock(standardInstant, ZoneId.systemDefault());
        jwtTokenManager = new JwtTokenManager(clock, jwtProperties);

        // when
        TokenInfo tokenInfo = jwtTokenManager.issue(memberId);

        // then
        validateToken(tokenInfo.accessToken(), standardInstant.plusSeconds(jwtProperties.accessTokenExpires()));
        validateToken(tokenInfo.refreshToken(), standardInstant.plusSeconds(jwtProperties.refreshTokenExpires()));
    }

    private void mockingClock(Instant instant, ZoneId zoneId) {
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(zoneId);
    }

    private void mockingJwtProperties(String secretKey, long accessExpires, long refreshExpires) {
        when(jwtProperties.secret()).thenReturn(secretKey);
        when(jwtProperties.accessTokenExpires()).thenReturn(accessExpires);
        when(jwtProperties.refreshTokenExpires()).thenReturn(refreshExpires);
    }

    private void validateToken(String token, Instant comparedInstant) {
        comparedInstant = comparedInstant.truncatedTo(ChronoUnit.SECONDS);
        Jwt jwtToken = parser.parse(token);
        Claims claims = (Claims) jwtToken.getBody();
        Instant expiration = claims.getExpiration().toInstant();
        assertThat(expiration).isEqualTo(comparedInstant);
    }
}