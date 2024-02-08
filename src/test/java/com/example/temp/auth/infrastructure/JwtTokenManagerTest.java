package com.example.temp.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.example.temp.auth.dto.MemberInfo;
import com.example.temp.auth.dto.response.TokenInfo;
import com.example.temp.auth.exception.TokenInvalidException;
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

    /**
     * mockingClock 메서드를 통해 테스트의 시점을 fixedMachineTime으로 고정시켰습니다.
     * mockingJwtProperties 메서드를 통해 accessToken의 수명을 1800초, refreshToken의 수명을 10000초로 고정시켰습니다.
     * key, parser는 테스트 과정에서 만들어진 토큰을 검증하기 위해 사용합니다.
     */
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

    @Test
    @DisplayName("우리 서버에서 서명되지 않은 Refresh Token으로는 TokenInfo를 재발급받을 수 없다.")
    void cantReIssueInvalidRefreshToken() throws Exception {
        // given
        long memberId = 1L;
        Date future = Date.from(fixedMachineTime.plusSeconds(100000L));
        String refreshToken = Jwts.builder()
            .subject(String.valueOf(memberId))
            .expiration(future)
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("invalidServerKeyinvalidServerKeyinvalidServerKey")))
            .compact();

        // when & then
        assertThatThrownBy(() -> jwtTokenManager.reIssue(refreshToken))
            .isInstanceOf(TokenInvalidException.class);
    }

    @Test
    @DisplayName("토큰을 파싱해서 id를 얻는다")
    void parse() throws Exception {
        // given
        long memberId = 1L;
        Date future = Date.from(fixedMachineTime.plusSeconds(100000L));
        String token = createToken(future, memberId);

        // when
        long result = jwtTokenManager.parse(token);

        // then
        assertThat(result).isEqualTo(memberId);
    }

    @Test
    @DisplayName("토큰을 파싱해서 memberInfo 객체를 얻는다.")
    void parsedClaims() throws Exception {
        // given
        long memberId = 1L;
        Date future = Date.from(fixedMachineTime.plusSeconds(100000L));
        String token = createToken(future, memberId);

        // when
        MemberInfo memberInfo = jwtTokenManager.parsedClaims(token);

        // then
        assertThat(memberInfo.id()).isEqualTo(memberId);
    }

    private String createToken(Date expired, long subject) {
        return Jwts.builder()
            .subject(String.valueOf(subject))
            .expiration(expired)
            .signWith(key)
            .compact();
    }

    /**
     * token의 subject와 expiration이 일치하는지 검증하는 메서드입니다.
     */
    private void validateToken(String token, long subject, Instant comparedMachineTime) {
        comparedMachineTime = removeNanoSecond(comparedMachineTime);
        Jws<Claims> claimsJws = parser.parseSignedClaims(token);
        Claims claims = claimsJws.getPayload();

        assertThat(claims.getSubject()).isEqualTo(String.valueOf(subject));

        Instant expiration = claims.getExpiration().toInstant();
        assertThat(expiration).isEqualTo(comparedMachineTime);
    }

    private Instant removeNanoSecond(Instant comparedInstant) {
        return comparedInstant.truncatedTo(ChronoUnit.SECONDS);
    }

    private void mockingClock(Instant instant, ZoneId zoneId) {
        lenient().when(clock.instant()).thenReturn(instant);
        lenient().when(clock.getZone()).thenReturn(zoneId);
    }

    private void mockingJwtProperties(String secretKey, long accessExpires, long refreshExpires) {
        when(jwtProperties.secret()).thenReturn(secretKey);
        lenient().when(jwtProperties.accessTokenExpires()).thenReturn(accessExpires);
        lenient().when(jwtProperties.refreshTokenExpires()).thenReturn(refreshExpires);
    }
}
