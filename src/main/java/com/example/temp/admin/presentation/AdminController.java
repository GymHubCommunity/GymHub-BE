package com.example.temp.admin.presentation;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.example.temp.admin.application.AdminService;
import com.example.temp.admin.dto.request.AdminLoginRequest;
import com.example.temp.admin.dto.request.AdminRegisterRequest;
import com.example.temp.auth.domain.Role;
import com.example.temp.auth.dto.response.TokenInfo;
import com.example.temp.auth.infrastructure.TokenManager;
import com.example.temp.auth.presentation.RefreshCookieProperties;
import com.example.temp.machine.application.MachineService;
import com.example.temp.machine.dto.request.MachineBulkCreateRequest;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    public static final String REFRESH = "refresh";

    private final RefreshCookieProperties refreshCookieProperties;
    private final MachineService machineService;
    private final AdminService adminService;
    private final TokenManager tokenManager;

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@RequestBody AdminLoginRequest request,
        HttpServletResponse response) {
        long loginUserId = adminService.login(request, LocalDateTime.now());
        TokenInfo tokenInfo = tokenManager.issueWithRole(loginUserId, Role.ADMIN);

        createRefreshCookie(tokenInfo.refreshToken(), response);
        return ResponseEntity.ok(tokenInfo);
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

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AdminRegisterRequest request) {
        adminService.register(request, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/machines/bulk")
    public ResponseEntity<List<MachineCreateResponse>> createMachinesBulk(MachineBulkCreateRequest request) {
        List<MachineCreateResponse> response = machineService.createMachinesBulk(request);
        return ResponseEntity.ok(response);
    }

}
