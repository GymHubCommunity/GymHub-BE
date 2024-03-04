package com.example.temp;

import com.example.temp.common.infrastructure.exceptionsender.ExceptionSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("test")
public class TestConfig {

    @Bean
    ExceptionSender dummyExceptionSender() {
        return (exception, request) -> {

        };
    }

}
