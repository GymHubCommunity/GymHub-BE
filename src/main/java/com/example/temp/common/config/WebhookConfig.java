package com.example.temp.common.config;

import com.example.temp.common.infrastructure.exceptionsender.DiscordExceptionSender;
import com.example.temp.common.infrastructure.exceptionsender.ExceptionSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class WebhookConfig {

    private final ObjectMapper objectMapper;

    @Bean
    ExceptionSender discordExceptionSender() {
        return new DiscordExceptionSender(objectMapper);
    }

}
