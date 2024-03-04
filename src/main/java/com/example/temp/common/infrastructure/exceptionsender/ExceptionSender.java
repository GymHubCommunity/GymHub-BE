package com.example.temp.common.infrastructure.exceptionsender;

import jakarta.servlet.http.HttpServletRequest;

public interface ExceptionSender {

    void send(Exception exception, HttpServletRequest request);
}
