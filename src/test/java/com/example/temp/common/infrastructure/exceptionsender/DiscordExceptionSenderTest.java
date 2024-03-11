package com.example.temp.common.infrastructure.exceptionsender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.example.temp.common.exception.ExceptionInfo;
import com.example.temp.common.exception.ExceptionSenderNotWorkingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class DiscordExceptionSenderTest {

    DiscordExceptionSender discordExceptionSender;

    WebClient webClient;

    static MockWebServer mockDiscord;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockDiscord = new MockWebServer();
        mockDiscord.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockDiscord.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = String.format("http://localhost:%s", mockDiscord.getPort());
        webClient = WebClient.create(baseUrl);
        discordExceptionSender = new DiscordExceptionSender(new ObjectMapper(), webClient);
    }

    @Test
    @DisplayName("ExceptionInfo를 전달에 성공함녀 NO_CONTENT 상태를 반환한다.")
    void sendExceptionInfo() throws Exception {
        // given
        ExceptionInfo exceptionInfo = createExceptionInfo();
        mockDiscord.enqueue(new MockResponse().setResponseCode(HttpStatus.NO_CONTENT.value()));

        // when & then
        assertDoesNotThrow(() -> discordExceptionSender.send(exceptionInfo));
    }

    @ParameterizedTest
    @DisplayName("요청의 결과로 NO_CONTENT 이외의 상태코드를 받으면 ExceptionSenderNotWorkingException를 던진다.")
    @ValueSource(ints = {200, 201, 400, 404, 500})
    void sendFail(int statusCode) throws Exception {
        // given
        ExceptionInfo exceptionInfo = createExceptionInfo();
        mockDiscord.enqueue(new MockResponse().setResponseCode(statusCode));

        // when & then
        Assertions.assertThatThrownBy(() -> discordExceptionSender.send(exceptionInfo))
            .isInstanceOf(ExceptionSenderNotWorkingException.class);
    }

    private ExceptionInfo createExceptionInfo() {
        return ExceptionInfo.builder()
            .clazz("clazz")
            .message("message")
            .requestUri("/hello")
            .method("POST")
            .userContextOpt(Optional.empty())
            .build();
    }
}