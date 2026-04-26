package com.ankrbde.employee_management_api.audit.error;

public class RetryableException extends RuntimeException {
    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}