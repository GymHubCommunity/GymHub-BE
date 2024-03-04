package com.example.temp.common.exception;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.infrastructure.exceptionsender.ExceptionSender;
import com.example.temp.common.interceptor.AuthenticationInterceptor;
import com.example.temp.member.exception.NicknameDuplicatedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ExceptionSender exceptionSender;

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

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestCookieException(MissingRequestCookieException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.create(String.format("%s 쿠키가 존재하지 않습니다.", exception.getCookieName())));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.create(
                String.format("'%s'의 타입이 잘못되었습니다.", exception.getParameter().getParameterName())));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.create("요청보내신 바디의 형태가 잘못되었습니다."));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException exception) {
        FieldError fieldError = getFirstFieldError(exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.create(
                String.format(String.format("[%s] %s", fieldError.getField(), fieldError.getDefaultMessage()))));
    }

    private FieldError getFirstFieldError(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return bindingResult.getFieldErrors().get(0);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerException(Exception exception, HttpServletRequest request) {
        log.error(exception.getMessage());
        sendExceptionInfo(exception, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.createServerError());
    }

    private void sendExceptionInfo(Exception exception, HttpServletRequest request){
        UserContext userContext = (UserContext) request.getAttribute(AuthenticationInterceptor.MEMBER_INFO);
        ExceptionInfo exceptionInfo = ExceptionInfo.builder()
            .clazz(exception.getClass().getName())
            .message(exception.getMessage())
            .requestUri(request.getRequestURI())
            .method(request.getMethod())
            .userContextOpt(Optional.ofNullable(userContext))
            .build();
        try {
            exceptionSender.send(exceptionInfo);
        } catch (JsonProcessingException e) {
            log.warn("예외 메세지 파싱에 실패했습니다.");
            log.warn(e.getMessage());
        }
    }

}
