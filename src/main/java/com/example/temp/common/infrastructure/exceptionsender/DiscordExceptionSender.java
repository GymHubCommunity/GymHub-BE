package com.example.temp.common.infrastructure.exceptionsender;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ExceptionInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class DiscordExceptionSender implements ExceptionSender {

    @Value("${webhookUrl}")
    private String webhookUrl;

    private final ObjectMapper objectMapper;

    @Override
    public void send(ExceptionInfo exceptionInfo) throws JsonProcessingException {
        String body = objectMapper.writeValueAsString(new BodyValue(exceptionInfo));
        WebClient client = WebClient.builder()
            .baseUrl(webhookUrl)
            .build();
        client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(body))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    @Getter
    static class BodyValue implements Serializable {

        private final List<Embed> embeds;

        public BodyValue(ExceptionInfo exceptionInfo) {
            embeds = List.of(new Embed(exceptionInfo));
        }

        @Getter
        static class Embed implements Serializable {

            private final String title;
            private final String description;

            public Embed(ExceptionInfo exceptionInfo) {
                this.title = exceptionInfo.getClazz();
                this.description = getDescription(exceptionInfo);
            }

            private String getDescription(ExceptionInfo exceptionInfo) {
                Optional<UserContext> userContextOpt = exceptionInfo.getUserContextOpt();
                return String.format(
                    """
                        **Endpoint**
                        %s
                        **Message**
                        %s
                        **Login User Info**
                        %s
                        """, exceptionInfo.getEndpoint(), exceptionInfo.getMessage(), getUserInfo(userContextOpt));
            }

            private String getUserInfo(Optional<UserContext> userContextOpt) {
                return userContextOpt.isPresent() ? userContextOpt.get().toString() : "로그인하지 않은 사용자";
            }
        }
    }
}
