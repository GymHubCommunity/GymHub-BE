package com.example.temp.auth.infrastructure;

import com.example.temp.auth.dto.response.TokenInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class JwtTokenManager implements TokenManager {

    private static final String BEARER = "Bearer";

    private final Clock clock;
    private final JwtProperties properties;
    private final Key key;

    @Autowired
    public JwtTokenManager(Clock clock, JwtProperties properties) {
        this.clock = clock;
        this.properties = properties;
        byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public TokenInfo issue(Long id) {
        LocalDateTime now = LocalDateTime.now(clock);
        String accessToken = makeToken(String.valueOf(id), properties.accessTokenExpires(), now);
        String refreshToken = makeToken(String.valueOf(id), properties.refreshTokenExpires(), now);
        return TokenInfo.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    String makeToken(String sub, long expires, LocalDateTime now) {
        LocalDateTime expiresDateTime = now.plusSeconds(expires);
        return Jwts.builder()
            .setSubject(sub)
            .setExpiration(Timestamp.valueOf(expiresDateTime))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    @Override
    public TokenInfo reIssue(String refreshToken) {
        return null;
    }
}
