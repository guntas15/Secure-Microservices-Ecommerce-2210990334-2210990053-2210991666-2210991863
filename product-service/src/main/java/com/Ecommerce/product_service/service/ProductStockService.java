package com.Ecommerce.product_service.service;

public interface ProductStockService {

    void updateStock(String productId, Integer stock);

    void reduceStock(String productId, int quantity);

    void restoreStock(String productId, int quantity);
}
