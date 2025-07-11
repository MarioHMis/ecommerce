package com.marware.ecommerce.exception;

public class FileProcessingException extends ApiException {

    public FileProcessingException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
