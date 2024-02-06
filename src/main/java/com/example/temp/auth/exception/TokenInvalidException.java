package com.example.temp.auth.exception;

public class TokenInvalidException extends RuntimeException {

    public static final String TOKEN_INVALID_MSG = "적절하지 않은 토큰입니다.";

    public TokenInvalidException(Throwable cause) {
        super(TOKEN_INVALID_MSG, cause);
    }
}
