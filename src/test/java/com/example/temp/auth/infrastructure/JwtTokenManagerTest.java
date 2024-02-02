package com.example.temp.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
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

    JwtParser parser;

    Instant fixedMachineTime;

    SecretKey key;

    @BeforeEach
    void setUp() {
        fixedMachineTime = Instant.parse("3000-12-03T10:15:30Z");
        String keyStr = "isTestSecretisTestSecretisTestSecretisTestSecretisTestSecret";

        mockingClock(fixedMachineTime, ZoneId.systemDefault());
        mockingJwtProperties(keyStr, 1800L, 10000L);

        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyStr));
        parser = Jwts.parser()
            .verifyWith(key)
            .build();

        jwtTokenManager = new JwtTokenManager(clock, jwtProperties);
    }

    @Test
    @DisplayName("access Token과 refresh Token을 생성한다.")
    void createTokenInfo() throws Exception {
        // given
        long memberId = 1L;

        // when
        TokenInfo tokenInfo = jwtTokenManager.issue(memberId);

        // then
        validateToken(tokenInfo.accessToken(), memberId,
            fixedMachineTime.plusSeconds(jwtProperties.accessTokenExpires()));
        validateToken(tokenInfo.refreshToken(), memberId,
            fixedMachineTime.plusSeconds(jwtProperties.refreshTokenExpires()));
    }

    @Test
    @DisplayName("만료되지 않은 refresh Token을 사용해서 access Token과 refresh Token을 재발급받는다.")
    void reIssueTokenInfo() throws Exception {
        // given
        long memberId = 1L;
        Date future = Date.from(fixedMachineTime.plusSeconds(100000L));
        String refreshToken = createToken(future, memberId);

        // when
        TokenInfo tokenInfo = jwtTokenManager.reIssue(refreshToken);

        // then
        validateToken(tokenInfo.accessToken(), memberId,
            fixedMachineTime.plusSeconds(jwtProperties.accessTokenExpires()));
        validateToken(tokenInfo.refreshToken(), memberId,
            fixedMachineTime.plusSeconds(jwtProperties.refreshTokenExpires()));
    }

    @Test
    @DisplayName("만료된 Refresh Token으로는 TokenInfo를 재발급받을 수 없다")
    void reIssueFailExpiredRefreshToken() throws Exception {
        // given
        long memberId = 1L;
        Date past = Date.from(fixedMachineTime.minusSeconds(100000L));
        String refreshToken = createToken(past, memberId);

        // when & then
        assertThatThrownBy(() -> jwtTokenManager.reIssue(refreshToken))
            .isInstanceOf(ExpiredJwtException.class);
    }

    // 내가 알지 못하는 키로 서명된 토큰이 들어왔을 때

    private String createToken(Date expired, long subject) {
        return Jwts.builder()
            .subject(String.valueOf(subject))
            .expiration(expired)
            .signWith(key)
            .compact();
    }

    private void validateToken(String token, long subject, Instant comparedInstant) {
        comparedInstant = removeNanoSecond(comparedInstant);
        Jws<Claims> claimsJws = parser.parseSignedClaims(token);
        Claims claims = claimsJws.getPayload();
        assertThat(claims.getSubject()).isEqualTo(String.valueOf(subject));
        Instant expiration = claims.getExpiration().toInstant();
        assertThat(expiration).isEqualTo(comparedInstant);
    }

    private Instant removeNanoSecond(Instant comparedInstant) {
        return comparedInstant.truncatedTo(ChronoUnit.SECONDS);
    }

    private void mockingClock(Instant instant, ZoneId zoneId) {
        when(clock.instant()).thenReturn(instant);
        lenient().when(clock.getZone()).thenReturn(zoneId);
    }

    private void mockingJwtProperties(String secretKey, long accessExpires, long refreshExpires) {
        when(jwtProperties.secret()).thenReturn(secretKey);
        lenient().when(jwtProperties.accessTokenExpires()).thenReturn(accessExpires);
        lenient().when(jwtProperties.refreshTokenExpires()).thenReturn(refreshExpires);
    }

}