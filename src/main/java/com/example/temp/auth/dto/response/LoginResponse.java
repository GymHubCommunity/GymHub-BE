package com.example.temp.auth.dto.response;

public record LoginResponse(
    String accessToken,
    boolean requiredAdditionalInfo,
    LoginMemberResponse userInfo
) {

    public static LoginResponse of(TokenInfo tokenInfo, LoginMemberResponse memberResponse) {
        return new LoginResponse(tokenInfo.accessToken(), memberResponse.init(), memberResponse);
    }
}
