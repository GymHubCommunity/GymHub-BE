package com.example.temp.common.config;

import com.example.temp.common.infrastructure.exceptionsender.DiscordExceptionSender;
import com.example.temp.common.infrastructure.exceptionsender.ExceptionSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class WebhookConfig {

    @Bean
    ExceptionSender discordExceptionSender() {
        return new DiscordExceptionSender();
    }

}
