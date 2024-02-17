package com.example.temp.admin.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.admin.domain.Admin;
import com.example.temp.admin.dto.request.AdminRegisterRequest;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired
    AdminService adminService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("어드민 가입 시 활성화되지 않은 어드민이 생성된다.")
    void registerSuccess() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        String username = "kim12";
        String rawPwd = "qwer123";
        AdminRegisterRequest request = new AdminRegisterRequest(username, rawPwd);

        // when
        long registeredId = adminService.register(request, now);

        // then
        Admin registered = em.find(Admin.class, registeredId);
        assertThat(registered.getLastLogin()).isEqualTo(now);
        assertThat(registered.getUsername()).isEqualTo(username);
        assertThat(registered.getPassword()).isNotEqualTo(rawPwd);
        assertThat(registered.isActivate()).isFalse();

        boolean isSamePwd = passwordEncoder.matches(rawPwd, registered.getPassword());
        assertThat(isSamePwd).isTrue();
    }

}