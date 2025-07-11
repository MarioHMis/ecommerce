package com.marware.ecommerce.exception;


import org.springframework.security.access.AccessDeniedException;
public class UnauthorizedOperationException extends AccessDeniedException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
