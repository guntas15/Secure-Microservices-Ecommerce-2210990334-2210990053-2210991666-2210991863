package com.Ecommerce.product_service.service;

import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.dto.response.SearchResponse;

import java.util.List;

public interface ProductQueryService {

    ProductResponse getProductById(String id);

    List<ProductResponse> getAllProducts();

    SearchResponse<ProductResponse> searchProducts(String keyword);
}
