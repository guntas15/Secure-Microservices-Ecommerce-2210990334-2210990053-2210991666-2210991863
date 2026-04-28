package com.Ecommerce.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String errorCode;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
