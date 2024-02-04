package com.example.temp.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerUnitTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ApiException이 들어오면 ErrorCode가 들고 있는 status와 message를 반환한다.")
    void handleApiException() throws Exception {
        // given
        ErrorCode errorCode = ErrorCode.TEST;
        ApiException exception = new ApiException(errorCode);

        // when
        ResponseEntity<ErrorResponse> response = handler.handleApiException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(errorCode.getHttpStatus());

        ErrorResponse body = response.getBody();
        assertThat(body.message()).isEqualTo(errorCode.getMessage());
    }
}