package com.example.temp.common.config;

import com.example.temp.common.convertor.StringToPrivacyPolicyConverter;
import com.example.temp.common.interceptor.AdminInterceptor;
import com.example.temp.common.interceptor.AuthenticationInterceptor;
import com.example.temp.common.properties.CorsProperties;
import com.example.temp.common.resolver.LoginUserArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    private final AuthenticationInterceptor authenticationInterceptor;

    private final AdminInterceptor adminInterceptor;

    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(corsProperties.allowedOrigins())
            .allowedMethods(corsProperties.allowedMethods())
            .allowedHeaders(corsProperties.allowedHeaders())
            .exposedHeaders(corsProperties.exposedHeaders())
            .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
            .addPathPatterns("/admin/**")
            .excludePathPatterns("/admin/register", "/admin/login");

        registry.addInterceptor(authenticationInterceptor)
            .excludePathPatterns("/oauth/**", "/auth/refresh")
            .excludePathPatterns("/admin/**")
            .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToPrivacyPolicyConverter());
    }
}
