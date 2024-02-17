package com.example.temp.admin.application;

import com.example.temp.admin.domain.Admin;
import com.example.temp.admin.dto.request.AdminLoginRequest;
import com.example.temp.admin.dto.request.AdminRegisterRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void login(AdminLoginRequest request) {
        // TODO
    }

    @Transactional
    public long register(AdminRegisterRequest request, LocalDateTime now) {
        Admin admin = Admin.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.pwd()))
            .lastLogin(now)
            .build();

        adminRepository.save(admin);
        return admin.getId();
    }

}
