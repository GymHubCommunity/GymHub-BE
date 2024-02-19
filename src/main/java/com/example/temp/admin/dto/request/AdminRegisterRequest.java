package com.example.temp.admin.dto.request;

import com.example.temp.admin.domain.Admin;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;

public record AdminRegisterRequest(
    String username,
    String pwd
) {

    public Admin toEntityWithEncoderAndTime(PasswordEncoder passwordEncoder, LocalDateTime now) {
        return Admin.builder()
            .username(username())
            .password(passwordEncoder.encode(pwd()))
            .lastLogin(now)
            .activate(false)
            .build();
    }
}
