package com.example.temp.auth.presentation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "refresh-cookie")
public record RefreshCookieProperties(
    boolean secure,
    int maxAge,
    String sameSite
) {

}
