package com.example.temp.admin.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.admin.domain.Admin;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdminRegisterRequestTest {

    @Test
    @DisplayName("생성자 순서를 테스트한다.")
    void create() throws Exception {
        // given
        String username = "username";
        String pwd = "pwd";

        // when
        AdminRegisterRequest request = new AdminRegisterRequest(username, pwd);

        // then
        assertThat(request.username()).isEqualTo(username);
        assertThat(request.pwd()).isEqualTo(pwd);
    }

    @Test
    @DisplayName("AdminRegisterRequest와 PasswordEncoder, 현재 시간을 사용해 Admin 객체를 생성한다.")
    void toEntityWithEncoderAndTime() throws Exception {
        // given
        PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
        LocalDateTime now = LocalDateTime.now();

        String username = "username";
        String rawPwd = "rawPwd";
        AdminRegisterRequest request = new AdminRegisterRequest(username, rawPwd);

        // when
        Admin admin = request.toEntityWithEncoderAndTime(pwdEncoder, now);

        // then
        boolean isSamePwd = pwdEncoder.matches(rawPwd, admin.getPassword());
        assertThat(isSamePwd).isTrue();

        assertThat(admin.getUsername()).isEqualTo(username);
        assertThat(admin.getLastLogin()).isEqualTo(now);
        assertThat(admin.isActivate()).isFalse();
    }
}