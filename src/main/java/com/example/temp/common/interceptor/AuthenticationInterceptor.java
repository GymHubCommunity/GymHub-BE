package com.example.temp.common.interceptor;

import com.example.temp.common.dto.UserContext;
import com.example.temp.auth.infrastructure.TokenParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import software.amazon.awssdk.utils.StringUtils;

@RequiredArgsConstructor
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String BEARER = "Bearer ";
    public static final String MEMBER_INFO = "memberInfo";

    private final TokenParser tokenParser;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if (StringUtils.equals(request.getMethod(), "OPTIONS")) {
            return true;
        }
        String accessTokenBeforeProcessing = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessTokenBeforeProcessing == null || !accessTokenBeforeProcessing.startsWith(BEARER)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        String accessToken = accessTokenBeforeProcessing.substring(BEARER.length());
        UserContext userContext = tokenParser.parsedClaims(accessToken);
        if (!userContext.isNormal()) {
            return false;
        }
        request.setAttribute(MEMBER_INFO, userContext);
        return true;
    }

}
