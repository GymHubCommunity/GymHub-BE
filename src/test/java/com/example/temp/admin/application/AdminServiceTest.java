package com.example.temp.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.admin.domain.Admin;
import com.example.temp.admin.dto.request.AdminRegisterRequest;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @DisplayName("어드민 가입 시 활성화되지 않은 어드민이 생성된다.")
    @ValueSource(strings = {"qwer@123", "!@#1a", "$%^1a", "&*-=1a"})
    void registerSuccess(String rawPwd) throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        String username = "kim12";
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

    @Test
    @DisplayName("어드민의 비밀번호는 4자리 이상이어야 한다.")
    void registerFailPwdTooShort() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        String username = "kim12";
        String rawPwd = "qwe";
        AdminRegisterRequest request = new AdminRegisterRequest(username, rawPwd);

        // when & then
        assertThatThrownBy(() -> adminService.register(request, now))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.ADMIN_PWD_TOO_SHORT.getMessage());
    }

    @Test
    @DisplayName("어드민의 비밀번호는 특수문자가 포함되어야 한다.")
    void registerFailPwdNoSpecialChar() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        String username = "kim12";
        String rawPwd = "qwer123";
        AdminRegisterRequest request = new AdminRegisterRequest(username, rawPwd);

        // when & then
        assertThatThrownBy(() -> adminService.register(request, now))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.ADMIN_PWD_INVALID.getMessage());
    }

    @Test
    @DisplayName("어드민의 비밀번호는 영문자가 포함되어야 한다.")
    void registerFailPwdNoAlphabet() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        String username = "kim12";
        String rawPwd = "12@3";
        AdminRegisterRequest request = new AdminRegisterRequest(username, rawPwd);

        // when & then
        assertThatThrownBy(() -> adminService.register(request, now))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.ADMIN_PWD_INVALID.getMessage());
    }

    @Test
    @DisplayName("어드민의 비밀번호는 숫자가 포함되어야 한다.")
    void registerFailPwdNoNum() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        String username = "kim12";
        String rawPwd = "qw@e";
        AdminRegisterRequest request = new AdminRegisterRequest(username, rawPwd);

        // when & then
        assertThatThrownBy(() -> adminService.register(request, now))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.ADMIN_PWD_INVALID.getMessage());
    }
}