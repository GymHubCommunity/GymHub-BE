package com.example.temp.auth.oauth.impl.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleToken(
    @JsonProperty("access_token")
    String accessToken
) {
}
