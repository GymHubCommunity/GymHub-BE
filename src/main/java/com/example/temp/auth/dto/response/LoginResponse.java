package com.example.temp.auth.dto.response;

public record LoginResponse(
    String accessToken,
    boolean requiredAdditionalInfo,
    MemberInfo userInfo
) {

    public static LoginResponse of(TokenInfo tokenInfo, MemberInfo memberResponse) {
        return new LoginResponse(tokenInfo.accessToken(), !memberResponse.registered(), memberResponse);
    }
}
