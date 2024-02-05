package com.example.temp.auth.presentation;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.example.temp.auth.dto.request.OAuthLoginRequest;
import com.example.temp.auth.dto.response.LoginMemberResponse;
import com.example.temp.auth.dto.response.LoginResponse;
import com.example.temp.auth.dto.response.TokenInfo;
import com.example.temp.auth.infrastructure.TokenManager;
import com.example.temp.oauth.application.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    public static final String REFRESH = "refresh";

    private final RefreshCookieProperties refreshCookieProperties;
    private final OAuthService oAuthService;
    private final TokenManager tokenManager;

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<LoginResponse> oauthLogin(@PathVariable String provider,
        @RequestBody OAuthLoginRequest request, HttpServletResponse response) {
        LoginMemberResponse memberResponse = oAuthService.login(provider, request.authCode());
        TokenInfo tokenInfo = tokenManager.issue(memberResponse.id());

        ResponseCookie refreshCookie = createRefreshCookie(tokenInfo.refreshToken());
        response.addHeader(SET_COOKIE, refreshCookie.toString());
        return ResponseEntity.ok(LoginResponse.of(tokenInfo, memberResponse));
    }

    private ResponseCookie createRefreshCookie(String value) {
        return ResponseCookie.from(REFRESH, value)
            .path("/")
            .httpOnly(true)
            .secure(refreshCookieProperties.secure())
            .maxAge(refreshCookieProperties.maxAge())
            .sameSite(refreshCookieProperties.sameSite())
            .build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenInfo> reissueToken(@CookieValue(REFRESH) String refreshToken,
        HttpServletResponse response) {
        TokenInfo tokenInfo = tokenManager.reIssue(refreshToken);
        ResponseCookie refreshCookie = createRefreshCookie(tokenInfo.refreshToken());
        response.addHeader(SET_COOKIE, refreshCookie.toString());
        return ResponseEntity.ok(tokenInfo);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        log.info("로그아웃 성공");
        return ResponseEntity.noContent().build();
    }

}
