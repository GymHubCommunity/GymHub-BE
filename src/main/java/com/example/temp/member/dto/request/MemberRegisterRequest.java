package com.example.temp.member.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record MemberRegisterRequest(
    @Nullable
    String profileUrl,

    @NotBlank
    String nickname
) {

}
