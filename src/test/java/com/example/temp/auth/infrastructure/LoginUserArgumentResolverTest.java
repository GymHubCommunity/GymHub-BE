package com.example.temp.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.resolver.LoginUserArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;

class LoginUserArgumentResolverTest {

    @Mock
    private MethodParameter parameter;

    private LoginUserArgumentResolver loginUserArgumentResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginUserArgumentResolver = new LoginUserArgumentResolver();
    }

    @DisplayName("로그인 어노테이션이 붙어 있으면 support메서드가 true를 리턴한다.")
    @Test
    void supportsParameterWhenIsAnnotation() {
        // given
        when(parameter.getParameterAnnotation(Login.class)).thenReturn(mock(Login.class));

        // when
        boolean supports = loginUserArgumentResolver.supportsParameter(parameter);

        // then
        assertThat(supports).isTrue();
    }

    @DisplayName("로그인 어노테이션이 붙어 있지 않으면 support메서드가 false를 리턴한다.")
    @Test
    void supportsParameterWhenIsNotAnnotation() {
        // given
        when(parameter.getParameterAnnotation(Login.class)).thenReturn(null);

        // when
        boolean supports = loginUserArgumentResolver.supportsParameter(parameter);

        // then
        assertThat(supports).isFalse();
    }
}