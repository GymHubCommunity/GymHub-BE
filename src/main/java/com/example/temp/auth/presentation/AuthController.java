package com.example.temp.auth.presentation;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.example.temp.auth.dto.request.OAuthLoginRequest;
import com.example.temp.auth.dto.response.AuthorizedUrl;
import com.example.temp.auth.dto.response.LoginResponse;
import com.example.temp.auth.dto.response.MemberInfo;
import com.example.temp.auth.dto.response.TokenInfo;
import com.example.temp.auth.infrastructure.TokenManager;
import com.example.temp.oauth.application.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
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
        MemberInfo memberResponse = oAuthService.login(provider, request.authCode());
        TokenInfo tokenInfo = tokenManager.issue(memberResponse.id());

        createRefreshCookie(tokenInfo.refreshToken(), response);
        return ResponseEntity.ok(LoginResponse.of(tokenInfo, memberResponse));
    }

    @GetMapping("/oauth/{provider}/authorized_url")
    public ResponseEntity<AuthorizedUrl> getAuthorizedUrl(@PathVariable String provider) {
        String authorizedUrl = oAuthService.getAuthorizedUrl(provider);
        return ResponseEntity.ok(AuthorizedUrl.createInstance(authorizedUrl));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenInfo> reissueToken(@CookieValue(REFRESH) String refreshToken,
        HttpServletResponse response) {
        TokenInfo tokenInfo = tokenManager.reIssue(refreshToken);
        createRefreshCookie(tokenInfo.refreshToken(), response);
        return ResponseEntity.ok(tokenInfo);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        removeRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void createRefreshCookie(String value, HttpServletResponse response) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH, value)
            .path("/")
            .httpOnly(true)
            .secure(refreshCookieProperties.secure())
            .maxAge(refreshCookieProperties.maxAge())
            .sameSite(refreshCookieProperties.sameSite())
            .build();
        response.addHeader(SET_COOKIE, refreshCookie.toString());
    }

    private void removeRefreshCookie(HttpServletResponse response) {
        ResponseCookie removedCookie = ResponseCookie.from(REFRESH, "")
            .path("/")
            .httpOnly(true)
            .secure(refreshCookieProperties.secure())
            .maxAge(0)
            .sameSite(refreshCookieProperties.sameSite())
            .build();
        response.addHeader(SET_COOKIE, removedCookie.toString());
    }

}
