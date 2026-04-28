package com.Ecommerce.product_service.service;

import com.Ecommerce.product_service.dto.request.ProductRequest;
import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.dto.response.SearchResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(String id);

    List<ProductResponse> getAllProducts();

    ProductResponse updateStock(String id, Integer stock);

    ProductResponse deleteProduct(String id);

    SearchResponse<ProductResponse> searchProducts(String keyword);

    void reduceStock(String productId, int quantity);

    void restoreStock(String productId, int quantity);
}
