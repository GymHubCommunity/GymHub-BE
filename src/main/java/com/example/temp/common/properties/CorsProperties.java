package com.example.temp.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@SuppressWarnings("java:S6218")
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
    String[] allowedOrigins,
    String[] allowedMethods,
    String[] allowedHeaders,
    String[] exposedHeaders
) {

}
