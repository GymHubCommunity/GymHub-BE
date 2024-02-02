package com.example.temp.oauth.impl.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleToken(
    @JsonProperty("access_token")
    String accessToken,
    @JsonProperty("token_type")
    String tokenType
) {

    public String getValueUsingAuthorizationHeader() {
        return String.format("%s %s", tokenType, accessToken);
    }
}

