package com.enterprise.app.exception.exceptions;

import com.enterprise.app.exception.codes.ErrorCodes;

public class BusinessException extends RuntimeException {
    private final ErrorCodes code;

    public BusinessException(String message, ErrorCodes code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(message, ErrorCodes.BAD_REQUEST);
    }

    public BusinessException(String message, ErrorCodes code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ErrorCodes getCode() {
        return code;
    }
}
