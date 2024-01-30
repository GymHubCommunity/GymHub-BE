package com.example.temp.auth.presentation;

import com.example.temp.auth.dto.response.AccessToken;
import com.example.temp.auth.dto.response.LoginInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthController {

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<LoginInfoResponse> oauthLogin(@PathVariable String provider) {
        log.info("Hello {}", provider);
        return ResponseEntity.ok().build();
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
