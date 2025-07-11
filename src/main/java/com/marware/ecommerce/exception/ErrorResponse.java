package com.marware.ecommerce.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private String errorCode;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    private List<String> details;

    public static ErrorResponse fromApiException(ApiException e, HttpStatus status, String path) {
        return ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getMessage())
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}
