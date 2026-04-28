package com.Ecommerce.product_service.service.impl;

import com.Ecommerce.product_service.dto.request.ProductRequest;
import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.entity.DeletedProduct;
import com.Ecommerce.product_service.entity.Product;
import com.Ecommerce.product_service.exception.ProductNotFoundException;
import com.Ecommerce.product_service.repository.DeletedProductRepository;
import com.Ecommerce.product_service.repository.ProductRepository;
import com.Ecommerce.product_service.service.ProductCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;
    private final DeletedProductRepository deletedProductRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Product product = mapToEntity(request);
        Product savedProduct = productRepository.save(product);

        log.info("Product {} is saved", savedProduct.getId());
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse deleteProduct(String id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));

        ProductResponse response = mapToResponse(product);

        DeletedProduct deletedProduct = DeletedProduct.builder()
                .originalProductId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .deletedAt(LocalDateTime.now())
                .build();

        deletedProductRepository.save(deletedProduct);
        productRepository.delete(product);

        return response;
    }

    private Product mapToEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}
