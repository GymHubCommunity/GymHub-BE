package com.example.temp.common.exception;

import com.example.temp.common.dto.UserContext;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ExceptionInfo {

    private String clazz;
    private String message;
    private String endpoint;
    private Optional<UserContext> userContextOpt;

    @Builder
    private ExceptionInfo(String clazz, String message, String requestUri, String method,
        Optional<UserContext> userContextOpt) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(message);
        Objects.requireNonNull(requestUri);
        Objects.requireNonNull(method);

        this.clazz = clazz;
        this.message = message;
        this.endpoint = String.format("%s %s", method, requestUri);
        this.userContextOpt = userContextOpt;
    }

}
