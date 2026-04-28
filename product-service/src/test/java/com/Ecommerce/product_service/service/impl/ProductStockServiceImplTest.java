package com.Ecommerce.product_service.service.impl;

import com.Ecommerce.product_service.entity.Product;
import com.Ecommerce.product_service.exception.InsufficientStockException;
import com.Ecommerce.product_service.exception.ProductNotFoundException;
import com.Ecommerce.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStockServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductStockServiceImpl productStockService;

    @Test
    void updateStock_shouldUpdateStock_whenProductExists() {
        Product product = Product.builder()
                .id("1")
                .stock(10)
                .build();

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        productStockService.updateStock("1", 20);

        assertEquals(20, product.getStock());
    }

    @Test
    void updateStock_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productStockService.updateStock("1", 10));
    }

    @Test
    void reduceStock_shouldReduceStock_whenSufficientStock() {
        Product product = Product.builder()
                .id("1")
                .stock(10)
                .build();

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        productStockService.reduceStock("1", 4);

        assertEquals(6, product.getStock());
    }

    @Test
    void reduceStock_shouldThrowException_whenInsufficientStock() {
        Product product = Product.builder()
                .id("1")
                .stock(3)
                .build();

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class,
                () -> productStockService.reduceStock("1", 5));
    }

    @Test
    void reduceStock_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productStockService.reduceStock("1", 2));
    }

    @Test
    void restoreStock_shouldIncreaseStock_whenProductExists() {
        Product product = Product.builder()
                .id("1")
                .stock(5)
                .build();

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        productStockService.restoreStock("1", 3);

        assertEquals(8, product.getStock());
    }

    @Test
    void restoreStock_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productStockService.restoreStock("1", 3));
    }
}
