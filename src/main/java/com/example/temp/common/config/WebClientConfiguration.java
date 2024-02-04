package com.example.temp.common.config;

import com.example.temp.oauth.impl.google.GoogleOAuthClient;
import com.example.temp.oauth.impl.kakao.KakaoOAuthClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfiguration {

    @Bean
    public GoogleOAuthClient googleOAuthClient() {
        return createHttpInterface(GoogleOAuthClient.class);
    }

    @Bean
    public KakaoOAuthClient kakaoOauthClient() {
        return createHttpInterface(KakaoOAuthClient.class);
    }

    private <T> T createHttpInterface(Class<T> clazz) {
        WebClient webClient = WebClient.builder()
            .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builder(WebClientAdapter.forClient(webClient))
            .build();
        return factory.createClient(clazz);
    }

}
