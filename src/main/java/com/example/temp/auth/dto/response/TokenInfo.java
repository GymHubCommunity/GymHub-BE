package com.example.temp.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import lombok.Builder;

public record TokenInfo(
    String accessToken,

    @JsonIgnore
    String refreshToken
) {

    @Builder
    public TokenInfo {
        Objects.requireNonNull(accessToken);
        Objects.requireNonNull(refreshToken);
    }
}
