package com.example.temp.common.config;

import com.example.temp.common.infrastructure.exceptionsender.DiscordExceptionSender;
import com.example.temp.common.infrastructure.exceptionsender.ExceptionSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class DiscordConfig {

    private final ObjectMapper objectMapper;


    @Value("${webhookUrl}")
    private String webhookUrl;

    @Bean
    public WebClient discordWebClient() {
        return WebClient.builder()
            .baseUrl(webhookUrl)
            .build();
    }

    @Bean
    ExceptionSender discordExceptionSender() {
        return new DiscordExceptionSender(objectMapper, discordWebClient());
    }

}
