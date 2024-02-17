package com.example.temp.auth.infrastructure;

import com.example.temp.auth.domain.Role;
import com.example.temp.auth.dto.response.TokenInfo;

public interface TokenManager {

    /**
     * 사용자 고유 id와 Role을 사용해 Token들을 발급한다.
     *
     * @param id
     * @param role
     */
    TokenInfo issueWithRole(long id, Role role);

    /**
     * refresh Token을 사용해 Token들을 재발급한다.
     *
     * @param refreshToken
     * @return TokenInfo
     */
    TokenInfo reIssue(String refreshToken);

}
