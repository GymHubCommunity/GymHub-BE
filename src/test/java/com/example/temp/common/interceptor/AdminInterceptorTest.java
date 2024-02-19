package com.example.temp.common.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.temp.auth.domain.Role;
import com.example.temp.auth.infrastructure.TokenParser;
import com.example.temp.common.dto.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AdminInterceptorTest {

    AdminInterceptor adminInterceptor;

    @Mock
    TokenParser tokenParser;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    UserContext adminContext;

    @BeforeEach
    void setUp() {
        adminInterceptor = new AdminInterceptor(tokenParser);
        adminContext = UserContext.builder()
            .id(1L)
            .role(Role.ADMIN)
            .build();
    }

    @Test
    @DisplayName("사용자가 유효한 토큰을 사용하면 통과한다.")
    void passSuccess() throws Exception {
        // given
        String token = "token";
        when(tokenParser.parsedClaims(token))
            .thenReturn(adminContext);
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn("Bearer " + token);

        // when
        boolean result = adminInterceptor.preHandle(request, response, null);

        // then
        assertThat(result).isTrue();
        verify(request, times(1)).setAttribute("memberInfo", adminContext);
    }

    @Test
    @DisplayName("인증 헤더에 토큰이 없으면 통과할 수 없다.")
    void passFailTokenNotFound() throws Exception {
        // given
        String token = "token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn(null);

        // when
        boolean result = adminInterceptor.preHandle(request, response, null);

        // then
        assertThat(result).isFalse();
        verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(request, never()).setAttribute(anyString(), anyLong());
        verify(tokenParser, never()).parse(anyString());
    }

    @Test
    @DisplayName("인증 헤더에 토큰이 Bearer로 시작하지 않으면 통과할 수 없다.")
    void passFailTokenNotStartBearer() throws Exception {
        // given
        String token = "token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn(token);

        // when
        boolean result = adminInterceptor.preHandle(request, response, null);

        // then
        assertThat(result).isFalse();
        verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(request, never()).setAttribute(anyString(), anyLong());
        verify(tokenParser, never()).parse(anyString());
    }

    @Test
    @DisplayName("어드민이 아닌 사용자는 AdminInterceptor를 통과할 수 없다.")
    void preHandleFailBecauseUserIsAdmin() throws Exception {
        UserContext normalUserContext = UserContext.builder()
            .role(Role.NORMAL)
            .id(1L)
            .build();

        String token = "token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn("Bearer " + token);
        when(tokenParser.parsedClaims(token))
            .thenReturn(normalUserContext);

        // when
        boolean result = adminInterceptor.preHandle(request, response, null);

        // then
        assertThat(result).isFalse();
        verify(request, never()).setAttribute("memberInfo", normalUserContext);

    }

    @Test
    @DisplayName("Http Method가 OPTIONS일 때, AdminInterceptor를 통과한다")
    void preHandleSuccessThatMethodIsOptions() throws Exception {
        UserContext adminContext = UserContext.builder()
            .role(Role.ADMIN)
            .id(1L)
            .build();

        when(request.getMethod())
            .thenReturn("OPTIONS");

        // when
        boolean result = adminInterceptor.preHandle(request, response, null);

        // then
        assertThat(result).isTrue();
    }

}