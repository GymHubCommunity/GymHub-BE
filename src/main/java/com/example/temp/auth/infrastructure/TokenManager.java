package com.example.temp.auth.infrastructure;

import com.example.temp.auth.dto.response.TokenInfo;

public interface TokenManager {

    /**
     * 사용자 고유 id를 사용해 Token들을 발급한다.
     *
     * @param id
     * @return TokenInfo
     */
    TokenInfo issue(Long id);

    /**
     * refresh Token을 사용해 Token들을 재발급한다.
     *
     * @param refreshToken
     * @return TokenInfo
     */
    TokenInfo reIssue(String refreshToken);
}
