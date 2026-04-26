package com.ankrbde.employee_management_api.audit.error;

public class NonRetryableException extends RuntimeException {
    public NonRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}