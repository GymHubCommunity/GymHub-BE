package com.example.temp.common.infrastructure.exceptionsender;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ExceptionInfo;
import com.example.temp.common.exception.ExceptionSenderNotWorkingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class DiscordExceptionSender implements ExceptionSender {

    private final ObjectMapper objectMapper;

    @Qualifier("discordWebClient")
    private final WebClient discordWebClient;

    /**
     * 등록된 디스코드 서버로 발생한 예외에 대한 정보를 전달합니다.
     *
     * @param exceptionInfo
     * @throws ExceptionSenderNotWorkingException : 메세지 전달 과정에서 오류가 발생했을 때 해당 예외를 던집니다.
     */
    @Override
    public void send(ExceptionInfo exceptionInfo) {
        String body = serialize(exceptionInfo);
        discordWebClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(body))
            .retrieve()
            .onStatus(
                response -> !response.isSameCodeAs(HttpStatus.NO_CONTENT),
                clientResponse -> {
                    throw new ExceptionSenderNotWorkingException();
                })
            .bodyToMono(Void.class)
            .block();
    }

    private String serialize(ExceptionInfo exceptionInfo) {
        try {
            return objectMapper.writeValueAsString(new BodyValue(exceptionInfo));
        } catch (JsonProcessingException e) {
            throw new ExceptionSenderNotWorkingException(e);
        }
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
