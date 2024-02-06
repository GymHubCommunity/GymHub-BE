package com.example.temp.common.interceptor;

import com.example.temp.auth.infrastructure.TokenParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String BEARER = "Bearer ";
    public static final String EXECUTOR = "executor";

    private final TokenParser tokenParser;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String accessTokenBeforeProcessing = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessTokenBeforeProcessing == null || !accessTokenBeforeProcessing.startsWith(BEARER)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        String accessToken = accessTokenBeforeProcessing.substring(BEARER.length());
        long memberId = tokenParser.parse(accessToken);
        request.setAttribute(EXECUTOR, memberId);
        return true;
    }

}
