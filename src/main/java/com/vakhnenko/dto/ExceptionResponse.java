package com.vakhnenko.dto;

import lombok.Getter;

@Getter
public class ExceptionResponse {
    private final int status;
    private final String message;
    private final String path;
    private final String method;

    public ExceptionResponse(int status, String message, String path, String method) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.method = method;
    }
}
