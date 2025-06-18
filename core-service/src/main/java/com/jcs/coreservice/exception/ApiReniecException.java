package com.jcs.coreservice.exception;

public class ApiReniecException extends RuntimeException {
    public ApiReniecException(String message) {
        super(message);
    }

    public ApiReniecException(String message, Throwable cause) {
        super(message, cause);
    }
}