package com.example.temp.oauth.impl.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoToken(
    @JsonProperty("access_token")
    String accessToken,
    @JsonProperty("token_type")
    String tokenType
) {

    public String getValueUsingAuthorizationHeader() {
        return String.format("%s %s", tokenType, accessToken);
    }
}
