package com.example.temp.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TokenInfo(
    String accessToken,

    @JsonIgnore
    String refreshToken
) {

}
