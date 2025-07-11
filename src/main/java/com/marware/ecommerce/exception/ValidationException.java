package com.marware.ecommerce.exception;

import java.util.List;

public class ValidationException extends ApiException {
    private final List<String> details;

    public ValidationException(String errorCode, String message, List<String> details) {
        super(errorCode, message);
        this.details = details;
    }

    public List<String> getDetails() {
        return details;
    }
}
