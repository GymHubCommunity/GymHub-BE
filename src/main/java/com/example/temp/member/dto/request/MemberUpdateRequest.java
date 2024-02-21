package com.example.temp.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberUpdateRequest(
    @NotNull
    String profileUrl,

    @NotBlank
    String nickname
) {

}
