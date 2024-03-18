package com.example.temp.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.common.exception.ErrorResponse;
import com.example.temp.common.exception.ExceptionSenderNotWorkingException;
import com.example.temp.common.exception.GlobalExceptionHandler;
import com.example.temp.common.infrastructure.exceptionsender.ExceptionSender;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerUnitTest {

    ExceptionSender exceptionSender = Mockito.mock(ExceptionSender.class);

    GlobalExceptionHandler handler = new GlobalExceptionHandler(exceptionSender);

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
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(requestMock.getRequestURI()).thenReturn("https://test");
        Mockito.when(requestMock.getMethod()).thenReturn("POST");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleServerException(exception, requestMock);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse body = response.getBody();
        assertThat(body.getMessage()).isEqualTo(ErrorResponse.SERVER_ERROR_MSG);
    }

@Test
@DisplayName("ExceptionSenderNotWorkingException이 발생하면 INTERNAL_SERVER_ERROR를 반환한다.")
void handleExceptionSenderNotWorkingException() throws Exception {
    // given

    ExceptionSenderNotWorkingException exception = Mockito.mock(ExceptionSenderNotWorkingException.class);
    Mockito.when(exception.getMessage()).thenReturn("메세지");

    // when
    ResponseEntity<ErrorResponse> response = handler.handleExceptionSenderNotWorkingException(exception);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
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