package com.example.temp.auth.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    long accessTokenExpires,
    long refreshTokenExpires
) {

}
