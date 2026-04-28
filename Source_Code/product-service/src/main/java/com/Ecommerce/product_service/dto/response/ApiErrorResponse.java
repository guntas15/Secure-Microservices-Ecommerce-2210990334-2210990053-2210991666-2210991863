package com.Ecommerce.product_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String errorCode;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
