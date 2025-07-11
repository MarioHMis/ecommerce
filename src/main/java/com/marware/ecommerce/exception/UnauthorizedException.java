package com.marware.ecommerce.exception;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String errorCode, String message) {
        super(errorCode, message);  // Pasa ambos parámetros al padre
    }

    public UnauthorizedException(String message) {
        this("UNAUTHORIZED", message); // Usa código por defecto
    }
}
