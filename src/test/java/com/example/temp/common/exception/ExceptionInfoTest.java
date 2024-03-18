package com.example.temp.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.auth.domain.Role;
import com.example.temp.common.dto.UserContext;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionInfoTest {

    String clazz = "RuntimeException";
    String method = "POST";
    String message = "예외입니다.";
    String requestUri = "/hello";
    UserContext userContext = UserContext.builder().id(1L).role(Role.NORMAL).build();

    @Test
    @DisplayName("클래스 필드는 값이 들어있어야 한다.")
    void clazzFieldCantNull() throws Exception {
        // when & then
        assertThatThrownBy(() -> createExceptionInfo(null, message, message, requestUri, userContext))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("메세지 필드는 값이 들어있어야 한다.")
    void messageFieldCantNull() throws Exception {
        // when & then
        assertThatThrownBy(() -> createExceptionInfo(clazz, null, message, requestUri, userContext))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("URI 필드는 값이 들어있어야 한다.")
    void uriFieldCantNull() throws Exception {
        // when & then
        assertThatThrownBy(() -> createExceptionInfo(clazz, message, null, requestUri, userContext))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("메서드 필드는 값이 들어있어야 한다.")
    void methodFieldCantNull() throws Exception {
        // when & then
        assertThatThrownBy(() -> createExceptionInfo(clazz, message, requestUri, null, userContext))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("UserContext 필드는 값이 없어도 된다")
    void userContextFieldCanNull() throws Exception {
        // when & then
        ExceptionInfo exceptionInfo = createExceptionInfo(clazz, message, requestUri, method, null);
        assertThat(exceptionInfo.getUserContextOpt()).isEmpty();
        assertThat(exceptionInfo.getClazz()).isNotNull();
        assertThat(exceptionInfo.getEndpoint()).isNotNull();
        assertThat(exceptionInfo.getMessage()).isNotNull();
    }

    @Test
    @DisplayName("ExceptionInfo를 생성한다.")
    void createExceptionInfo() throws Exception {
        // when & then
        ExceptionInfo exceptionInfo = createExceptionInfo(clazz, message, requestUri, method, userContext);
        assertThat(exceptionInfo.getUserContextOpt()).containsSame(userContext);
        assertThat(exceptionInfo.getClazz()).isEqualTo(clazz);
        assertThat(exceptionInfo.getEndpoint()).isEqualTo(method + " " + requestUri);
        assertThat(exceptionInfo.getMessage()).isEqualTo(message);
    }

    private ExceptionInfo createExceptionInfo(String clazz, String message, String requestUri, String method,
        UserContext userContext) {
        return ExceptionInfo.builder()
            .clazz(clazz)
            .message(message)
            .requestUri(requestUri)
            .method(method)
            .userContextOpt(Optional.ofNullable(userContext))
            .build();
    }

}