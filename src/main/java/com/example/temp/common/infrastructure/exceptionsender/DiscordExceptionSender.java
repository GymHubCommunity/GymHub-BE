package com.example.temp.common.infrastructure.exceptionsender;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class DiscordExceptionSender implements ExceptionSender {

    @Value("${webhookUrl}")
    String webhookUrl;

    @Override
    public void send(Exception exception, HttpServletRequest request) {
        String message = "예외 발생: " + exception.getMessage();
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
