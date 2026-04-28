package com.Ecommerce.product_service.service.impl;

import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.dto.response.SearchResponse;
import com.Ecommerce.product_service.entity.Product;
import com.Ecommerce.product_service.exception.ProductNotFoundException;
import com.Ecommerce.product_service.repository.ProductRepository;
import com.Ecommerce.product_service.service.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SearchResponse<ProductResponse> searchProducts(String keyword) {

        List<Product> products =
                productRepository
                        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                                keyword, keyword
                        );

        List<ProductResponse> responseList = products.stream()
                .map(this::mapToResponse)
                .toList();

        return responseList.isEmpty()
                ? new SearchResponse<>("No products found", 0, responseList)
                : new SearchResponse<>("Results found", responseList.size(), responseList);
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
