package com.Ecommerce.order_service.service.impl;

import com.Ecommerce.order_service.client.ProductClient;
import com.Ecommerce.order_service.dto.request.ReduceStockRequest;
import com.Ecommerce.order_service.dto.request.RestoreStockRequest;
import com.Ecommerce.order_service.dto.response.ProductResponse;
import com.Ecommerce.order_service.service.ProductStockService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductStockServiceImpl implements ProductStockService {

    private final ProductClient productClient;

    @Override
    public ProductResponse getProduct(String productId, String authHeader) {
        return productClient.getProductById(productId, authHeader);
    }

    @Override
    public void reduceStock(String productId, Integer quantity, String authHeader) {
        try {
            productClient.reduceStock(
                    productId,
                    new ReduceStockRequest(productId, quantity),
                    authHeader
            );
        } catch (FeignException.BadRequest ex) {
            throw new IllegalStateException("Cannot place order: insufficient stock");
        }
    }

    @Override
    public void restoreStock(String productId, Integer quantity, String authHeader) {
        productClient.restoreStock(
                productId,
                new RestoreStockRequest(productId, quantity),
                authHeader
        );
    }
}
