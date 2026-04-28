package com.Ecommerce.order_service.client;

import com.Ecommerce.order_service.dto.request.ReduceStockRequest;
import com.Ecommerce.order_service.dto.request.RestoreStockRequest;
import com.Ecommerce.order_service.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service")   // must match Eureka
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(
            @PathVariable("id") String productId,
            @RequestHeader("Authorization") String authHeader
    );

    @PutMapping("/api/products/{id}/reduce-stock")
    void reduceStock(
            @PathVariable("id") String productId,
            @RequestBody ReduceStockRequest request,
            @RequestHeader("Authorization") String authHeader
    );

    @PutMapping("/api/products/{id}/restore-stock")
    void restoreStock(
            @PathVariable("id") String productId,
            @RequestBody RestoreStockRequest request,
            @RequestHeader("Authorization") String authHeader
    );
}

