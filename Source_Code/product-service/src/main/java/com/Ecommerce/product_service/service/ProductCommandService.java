package com.Ecommerce.product_service.service;

import com.Ecommerce.product_service.dto.request.ProductRequest;
import com.Ecommerce.product_service.dto.response.ProductResponse;

public interface ProductCommandService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse deleteProduct(String id);
}
