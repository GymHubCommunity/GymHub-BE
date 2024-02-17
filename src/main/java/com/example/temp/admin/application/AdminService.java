package com.example.temp.admin.application;

import com.example.temp.admin.domain.Admin;
import com.example.temp.admin.dto.request.AdminLoginRequest;
import com.example.temp.admin.dto.request.AdminRegisterRequest;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    public static final int PWD_MIN_LENGTH = 4;
    public static final String PWD_REGEX = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*\\-+=]).*$";

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void login(AdminLoginRequest request) {
        // TODO
    }

    @Transactional
    public long register(AdminRegisterRequest request, LocalDateTime now) {
        validatePwd(request.pwd());
        Admin admin = Admin.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.pwd()))
            .lastLogin(now)
            .build();

        adminRepository.save(admin);
        return admin.getId();
    }

    private void validatePwd(String pwd) {
        if (pwd == null || pwd.length() < PWD_MIN_LENGTH) {
            throw new ApiException(ErrorCode.ADMIN_PWD_TOO_SHORT);
        }
        if (!pwd.matches(PWD_REGEX)) {
            throw new ApiException(ErrorCode.ADMIN_PWD_INVALID);
        }
    }

}
