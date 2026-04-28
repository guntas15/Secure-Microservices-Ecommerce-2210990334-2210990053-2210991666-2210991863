package com.Ecommerce.product_service.service.impl;

import com.Ecommerce.product_service.entity.Product;
import com.Ecommerce.product_service.exception.InsufficientStockException;
import com.Ecommerce.product_service.exception.ProductNotFoundException;
import com.Ecommerce.product_service.repository.ProductRepository;
import com.Ecommerce.product_service.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStockServiceImpl implements ProductStockService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void updateStock(String productId, Integer stock) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id: " + productId));

        product.setStock(stock);
    }

    @Override
    @Transactional
    public void reduceStock(String productId, int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock");
        }

        product.setStock(product.getStock() - quantity);
    }

    @Override
    @Transactional
    public void restoreStock(String productId, int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        product.setStock(product.getStock() + quantity);
    }
}
