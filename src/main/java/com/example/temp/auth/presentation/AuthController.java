package com.example.temp.auth.presentation;

import com.example.temp.auth.dto.request.OAuthLoginRequest;
import com.example.temp.auth.dto.response.AccessToken;
import com.example.temp.auth.dto.response.LoginMemberResponse;
import com.example.temp.auth.dto.response.LoginResponse;
import com.example.temp.auth.dto.response.TokenInfo;
import com.example.temp.auth.infrastructure.TokenManager;
import com.example.temp.oauth.application.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final OAuthService oAuthService;
    private final TokenManager tokenManager;

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<LoginResponse> oauthLogin(@PathVariable String provider,
        @RequestBody OAuthLoginRequest request) {
        LoginMemberResponse memberResponse = oAuthService.login(provider, request.authCode());
        TokenInfo tokenInfo = tokenManager.issue(memberResponse.id());
        // TODO refresh로 쿠키를 만든다
        return ResponseEntity.ok(LoginResponse.of(tokenInfo, memberResponse));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AccessToken> reissueToken() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        log.info("로그아웃 성공");
        return ResponseEntity.noContent().build();
    }

}
