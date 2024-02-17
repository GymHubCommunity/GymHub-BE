package com.example.temp.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.example.temp.admin.domain.Admin;
import com.example.temp.admin.dto.request.AdminLoginRequest;
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

    @Test
    @DisplayName("어드민은 id와 pwd를 사용해서 로그인할 수 있다.")
    void login() throws Exception {
        // given
        String username = "username";
        String rawPwd = "rawPwd";
        String encodedPwd = passwordEncoder.encode(rawPwd);
        LocalDateTime past = LocalDateTime.of(2010, 1, 1, 1, 1);
        Admin admin = createActivateAdmin(username, encodedPwd, past);

        LocalDateTime now = LocalDateTime.now();
        AdminLoginRequest request = new AdminLoginRequest(username, rawPwd);

        // when & then
        assertDoesNotThrow(() -> adminService.login(request, now));
        Admin loginAdmin = em.find(Admin.class, admin.getId());
        assertThat(loginAdmin.getLastLogin()).isEqualTo(now);
    }

    @Test
    @DisplayName("존재하지 않는 id로는 로그인할 수 없다.")
    void loginFailUsernameNotFound() throws Exception {
        // given
        String username = "username";
        String rawPwd = "rawPwd";

        LocalDateTime now = LocalDateTime.now();
        AdminLoginRequest request = new AdminLoginRequest(username, rawPwd);

        // when & then
        assertThatThrownBy(() -> adminService.login(request, now))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.ADMIN_LOGIN_FAIL.getMessage());
    }

    @Test
    @DisplayName("비밀번호를 잘못 입력하면 로그인할 수 없다.")
    void loginFailPwdMismatch() throws Exception {
        // given
        String username = "username";
        String rawPwd = "rawPwd";
        String encodedPwd = passwordEncoder.encode(rawPwd);
        LocalDateTime past = LocalDateTime.of(2010, 1, 1, 1, 1);
        createActivateAdmin(username, encodedPwd, past);

        LocalDateTime now = LocalDateTime.now();
        AdminLoginRequest request = new AdminLoginRequest(username, "invalidPwd");

        // when & then
        assertThatThrownBy(() -> adminService.login(request, now))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.ADMIN_LOGIN_FAIL.getMessage());
    }

    private Admin createActivateAdmin(String username, String encodedPwd, LocalDateTime past) {
        return createAdminHelper(username, encodedPwd, past, true);
    }

    private Admin createInactivateAdmin(String username, String encodedPwd, LocalDateTime past) {
        return createAdminHelper(username, encodedPwd, past, false);
    }

    private Admin createAdminHelper(String username, String encodedPwd, LocalDateTime past, boolean activate) {
        Admin admin = Admin.builder()
            .username(username)
            .password(encodedPwd)
            .lastLogin(past)
            .activate(activate)
            .build();
        em.persist(admin);
        return admin;
    }
}