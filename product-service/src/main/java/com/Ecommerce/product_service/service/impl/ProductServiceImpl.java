package com.Ecommerce.product_service.service.impl;

import com.Ecommerce.product_service.dto.request.ProductRequest;
import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.dto.response.SearchResponse;
import com.Ecommerce.product_service.service.ProductCommandService;
import com.Ecommerce.product_service.service.ProductQueryService;
import com.Ecommerce.product_service.service.ProductService;
import com.Ecommerce.product_service.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;
    private final ProductStockService productStockService;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        return productCommandService.createProduct(request);
    }

    @Override
    public ProductResponse getProductById(String id) {
        return productQueryService.getProductById(id);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productQueryService.getAllProducts();
    }

    @Override
    public ProductResponse updateStock(String id, Integer stock) {
        productStockService.updateStock(id, stock);
        return getProductById(id);
    }

    @Override
    public ProductResponse deleteProduct(String id) {
        return productCommandService.deleteProduct(id);
    }

    @Override
    public SearchResponse<ProductResponse> searchProducts(String keyword) {
        return productQueryService.searchProducts(keyword);
    }

    @Override
    public void reduceStock(String productId, int quantity) {
        productStockService.reduceStock(productId, quantity);
    }

    @Override
    public void restoreStock(String productId, int quantity) {
        productStockService.restoreStock(productId, quantity);
    }
}
