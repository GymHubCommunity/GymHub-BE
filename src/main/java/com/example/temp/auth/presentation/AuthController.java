package com.example.temp.auth.presentation;

import com.example.temp.auth.application.OAuthService;
import com.example.temp.auth.dto.request.OAuthLoginRequest;
import com.example.temp.auth.dto.response.AccessToken;
import com.example.temp.auth.dto.response.LoginInfoResponse;
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

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<LoginInfoResponse> oauthLogin(@PathVariable String provider,
        @RequestBody OAuthLoginRequest request) {
        LoginInfoResponse response = oAuthService.login(provider, request.authCode());
        return ResponseEntity.ok(response);
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
