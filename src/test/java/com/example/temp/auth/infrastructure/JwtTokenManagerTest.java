package com.example.temp.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.temp.auth.dto.response.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
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

    SecretKey key;

    JwtParser parser;

    Long memberId;

    Instant standardInstant;

    String secretKey;


    @BeforeEach
    void setUp() {
        memberId = 1L;
        standardInstant = Instant.parse("3000-12-03T10:15:30Z");
        secretKey = "isTestSecretisTestSecretisTestSecretisTestSecretisTestSecret";
    }

    @Test
    @DisplayName("access Token과 refresh Token을 생성한다.")
    void createTokenInfo() throws Exception {
        // given
        mockingClock(standardInstant, ZoneId.systemDefault());
        mockingJwtProperties(secretKey, 1800L, 10000L);
        jwtTokenManager = new JwtTokenManager(clock, jwtProperties);
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        parser = Jwts.parser()
            .verifyWith(key)
            .build();

        // when
        TokenInfo tokenInfo = jwtTokenManager.issue(memberId);

        // then
        validateToken(tokenInfo.accessToken(), standardInstant.plusSeconds(jwtProperties.accessTokenExpires()));
        validateToken(tokenInfo.refreshToken(), standardInstant.plusSeconds(jwtProperties.refreshTokenExpires()));
    }

    @Test
    @DisplayName("refresh Token을 사용해서 access Token과 refresh Token을 재발급받는다.")
    void reIssueTokenInfo() throws Exception {
        // given
        mockingClock(standardInstant, ZoneId.systemDefault());
        mockingJwtProperties(secretKey, 1800L, 10000L);

        jwtTokenManager = new JwtTokenManager(clock, jwtProperties);
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        parser = Jwts.parser()
            .verifyWith(key)
            .build();

        Date future = Date.from(standardInstant.plusSeconds(100000L));
        String refreshToken = Jwts.builder()
            .subject(String.valueOf(memberId))
            .expiration(future)
            .signWith(key)
            .compact();

        // when
        TokenInfo tokenInfo = jwtTokenManager.reIssue(refreshToken);

        // then
        validateToken(tokenInfo.accessToken(), standardInstant.plusSeconds(jwtProperties.accessTokenExpires()));
        validateToken(tokenInfo.refreshToken(), standardInstant.plusSeconds(jwtProperties.refreshTokenExpires()));
    }

    @Test
    @DisplayName("만료된 Refresh Token으로는 TokenInfo를 재발급받을 수 없다")
    void reIssueFailExpiredRefreshToken() throws Exception {
        // given
        when(clock.instant()).thenReturn(standardInstant);
        when(jwtProperties.secret()).thenReturn(secretKey);

        jwtTokenManager = new JwtTokenManager(clock, jwtProperties);
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        parser = Jwts.parser()
            .verifyWith(key)
            .build();

        Date past = Date.from(standardInstant.minusSeconds(100000L));
        String refreshToken = Jwts.builder()
            .subject(String.valueOf(memberId))
            .expiration(past)
            .signWith(key)
            .compact();

        // when & then
        assertThatThrownBy(() -> jwtTokenManager.reIssue(refreshToken))
            .isInstanceOf(ExpiredJwtException.class);
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
        Jws<Claims> claimsJws = parser.parseSignedClaims(token);
        Claims claims = claimsJws.getPayload();
        Instant expiration = claims.getExpiration().toInstant();
        assertThat(expiration).isEqualTo(comparedInstant);
    }
}