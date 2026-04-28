package com.Ecommerce.product_service.service.impl;

import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.dto.response.SearchResponse;
import com.Ecommerce.product_service.entity.Product;
import com.Ecommerce.product_service.exception.ProductNotFoundException;
import com.Ecommerce.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductQueryServiceImpl productQueryService;

    @Test
    void getProductById_returnsProduct_whenFound() {
        Product product = Product.builder()
                .id("1")
                .name("Phone")
                .description("Smart phone")
                .price(BigDecimal.valueOf(999.0))
                .stock(10)
                .build();

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productQueryService.getProductById("1");

        assertNotNull(response);
        assertEquals("Phone", response.getName());
        assertEquals(10, response.getStock());
    }

    @Test
    void getProductById_throwsException_whenNotFound() {
        when(productRepository.findById("99"))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productQueryService.getProductById("99"));
    }

    @Test
    void getAllProducts_returnsProductList() {
        Product product1 = Product.builder()
                .id("1")
                .name("Phone")
                .description("Smart phone")
                .price(BigDecimal.valueOf(999.0))
                .stock(10)
                .build();

        Product product2 = Product.builder()
                .id("2")
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(1999.0))
                .stock(5)
                .build();

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<ProductResponse> responses =
                productQueryService.getAllProducts();

        assertEquals(2, responses.size());
        assertEquals("Phone", responses.get(0).getName());
    }

    @Test
    void getAllProducts_returnsEmptyList_whenNoProducts() {
        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductResponse> responses =
                productQueryService.getAllProducts();

        assertTrue(responses.isEmpty());
    }

    @Test
    void searchProducts_returnsResults_whenFound() {
        Product product = Product.builder()
                .id("1")
                .name("Phone")
                .description("Smart phone")
                .price(BigDecimal.valueOf(999.0))
                .stock(10)
                .build();

        when(productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "phone", "phone"))
                .thenReturn(List.of(product));

        SearchResponse<ProductResponse> response =
                productQueryService.searchProducts("phone");

        assertEquals("Results found", response.getMessage());
        assertEquals(1, response.getCount());
        assertFalse(response.getData().isEmpty());
    }

    @Test
    void searchProducts_returnsNoResults_whenEmpty() {
        when(productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "xyz", "xyz"))
                .thenReturn(List.of());

        SearchResponse<ProductResponse> response =
                productQueryService.searchProducts("xyz");

        assertEquals("No products found", response.getMessage());
        assertEquals(0, response.getCount());
        assertTrue(response.getData().isEmpty());
    }
}
