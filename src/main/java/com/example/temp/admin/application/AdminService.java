package com.example.temp.admin.application;

import com.example.temp.admin.dto.request.AdminLoginRequest;
import com.example.temp.admin.dto.request.AdminRegisterRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public void login(AdminLoginRequest request) {
    }
}
