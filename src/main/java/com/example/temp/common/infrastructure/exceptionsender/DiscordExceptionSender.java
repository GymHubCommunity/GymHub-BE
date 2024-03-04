package com.example.temp.common.infrastructure.exceptionsender;

import com.example.temp.common.exception.ExceptionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class DiscordExceptionSender implements ExceptionSender {

    @Value("${webhookUrl}")
    String webhookUrl;

    @Override
    public void send(ExceptionInfo exceptionInfo) {
        String message = "예외 발생: " + exceptionInfo.getMessage();
        WebClient client = WebClient.builder()
            .baseUrl(webhookUrl)
            .build();
        client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue("{\"content\":\"" + message + "\"}"))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }
}
