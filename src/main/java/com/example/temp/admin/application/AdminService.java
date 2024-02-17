package com.example.temp.admin.application;

import com.example.temp.admin.dto.request.AdminLoginRequest;
import com.example.temp.admin.dto.request.AdminRegisterRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService {

    public void register(AdminRegisterRequest request) {
    }

    public void login(AdminLoginRequest request) {
    }
}
