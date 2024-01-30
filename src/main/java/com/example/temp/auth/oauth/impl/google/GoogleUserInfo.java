package com.example.temp.auth.oauth.impl.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfo(
    @JsonProperty("picture")
    String profileUrl,

    @JsonProperty("email")
    String email,

    @JsonProperty("sub")
    String idUsingResourceServer,

    @JsonProperty("name")
    String name
) {

}
