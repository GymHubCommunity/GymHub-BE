package com.example.temp.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerUnitTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleApiException이 실행되면 ErrorCode가 들고 있는 status와 message를 반환한다.")
    void handleApiException() throws Exception {
        // given
        ErrorCode errorCode = ErrorCode.TEST;
        ApiException exception = new ApiException(errorCode);

        // when
        ResponseEntity<ErrorResponse> response = handler.handleApiException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(errorCode.getHttpStatus());

        ErrorResponse body = response.getBody();
        assertThat(body.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    @DisplayName("handleServerException가 들어오면 INTERNAL_SERVER_ERROR와 함께 서버에서 에러가 발생했다는 메세지를 반환한다")
    void handleException() throws Exception {
        // given
        Exception exception = new Exception("에러에러");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleServerException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse body = response.getBody();
        assertThat(body.getMessage()).isEqualTo(ErrorResponse.SERVER_ERROR_MSG);

    }

    @Test
    @DisplayName("handleBadRequestStatus가 실행되면 BAD_REQUEST 상태를 반환한다")
    void handleNicknameDup() throws Exception {
        // given
        RuntimeException exception = new RuntimeException("msg");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleBadRequestStatus(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse body = response.getBody();
        assertThat(body.getMessage()).isEqualTo(exception.getMessage());

    }
}