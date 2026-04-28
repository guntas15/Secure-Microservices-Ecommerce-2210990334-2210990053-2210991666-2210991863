package com.Ecommerce.product_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductListResponse {
    private String message;
    private int count;
    private List<ProductResponse> products;
}
