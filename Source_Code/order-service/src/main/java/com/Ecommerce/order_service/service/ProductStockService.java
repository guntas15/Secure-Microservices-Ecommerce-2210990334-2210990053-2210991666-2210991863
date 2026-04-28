package com.Ecommerce.order_service.service;

import com.Ecommerce.order_service.dto.response.ProductResponse;

public interface ProductStockService {

    ProductResponse getProduct(String productId, String authHeader);

    void reduceStock(String productId, Integer quantity, String authHeader);

    void restoreStock(String productId, Integer quantity, String authHeader);
}
