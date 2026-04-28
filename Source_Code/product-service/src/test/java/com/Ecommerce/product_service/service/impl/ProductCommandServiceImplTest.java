package com.Ecommerce.product_service.service.impl;

import com.Ecommerce.product_service.dto.request.ProductRequest;
import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.entity.DeletedProduct;
import com.Ecommerce.product_service.entity.Product;
import com.Ecommerce.product_service.exception.ProductNotFoundException;
import com.Ecommerce.product_service.repository.DeletedProductRepository;
import com.Ecommerce.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DeletedProductRepository deletedProductRepository;

    @InjectMocks
    private ProductCommandServiceImpl productCommandService;

    @Test
    void createProduct_savesAndReturnsProductInfo() {
        ProductRequest request = ProductRequest.builder()
                .name("Phone")
                .description("Smart phone")
                .price(BigDecimal.valueOf(1000.0))
                .stock(10)
                .build();

        Product savedProduct = Product.builder()
                .id("1")
                .name("Phone")
                .description("Smart phone")
                .price(BigDecimal.valueOf(1000.0))
                .stock(10)
                .build();

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response =
                productCommandService.createProduct(request);

        assertNotNull(response);
        assertEquals("Phone", response.getName());
        assertEquals(10, response.getStock());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProduct_deletesAndArchivesProduct() {
        Product product = Product.builder()
                .id("1")
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(2000.0))
                .stock(5)
                .build();

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productCommandService.deleteProduct("1");

        assertNotNull(response);
        assertEquals("Laptop", response.getName());

        verify(deletedProductRepository)
                .save(any(DeletedProduct.class));
        verify(productRepository)
                .delete(product);
    }

    @Test
    void deleteProduct_throwsException() {
        when(productRepository.findById("99"))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productCommandService.deleteProduct("99"));

        verify(deletedProductRepository, never())
                .save(any());
        verify(productRepository, never())
                .delete(any());
    }
}
