package com.example.temp.exception;

import com.example.temp.member.exception.NicknameDuplicatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
            .body(ErrorResponse.create(exception.getMessage()));
    }

    @ExceptionHandler({NicknameDuplicatedException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestStatus(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.create(exception.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleServerException(Exception exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.createServerError());
    }
}
