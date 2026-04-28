package com.Ecommerce.product_service.service.impl;

import com.Ecommerce.product_service.dto.request.ProductRequest;
import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.dto.response.SearchResponse;
import com.Ecommerce.product_service.service.ProductCommandService;
import com.Ecommerce.product_service.service.ProductQueryService;
import com.Ecommerce.product_service.service.ProductStockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductCommandService productCommandService;

    @Mock
    private ProductQueryService productQueryService;

    @Mock
    private ProductStockService productStockService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_shouldDelegateToCommandService() {
        ProductRequest request = new ProductRequest();
        ProductResponse response = ProductResponse.builder().id("1").build();

        when(productCommandService.createProduct(request))
                .thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertEquals(response, result);
        verify(productCommandService).createProduct(request);
    }

    @Test
    void getProductById_shouldDelegateToQueryService() {
        ProductResponse response = ProductResponse.builder().id("1").build();

        when(productQueryService.getProductById("1"))
                .thenReturn(response);

        ProductResponse result = productService.getProductById("1");

        assertEquals(response, result);
        verify(productQueryService).getProductById("1");
    }

    @Test
    void getAllProducts_shouldDelegateToQueryService() {
        List<ProductResponse> products =
                List.of(ProductResponse.builder().id("1").build());

        when(productQueryService.getAllProducts())
                .thenReturn(products);

        List<ProductResponse> result = productService.getAllProducts();

        assertEquals(products, result);
        verify(productQueryService).getAllProducts();
    }

    @Test
    void updateStock_shouldUpdateAndReturnUpdatedProduct() {
        ProductResponse response = ProductResponse.builder()
                .id("1")
                .stock(20)
                .build();

        when(productQueryService.getProductById("1"))
                .thenReturn(response);

        ProductResponse result = productService.updateStock("1", 20);

        verify(productStockService).updateStock("1", 20);
        verify(productQueryService).getProductById("1");

        assertEquals(response, result);
    }

    @Test
    void deleteProduct_shouldDelegateToCommandService() {
        ProductResponse response = ProductResponse.builder().id("1").build();

        when(productCommandService.deleteProduct("1"))
                .thenReturn(response);

        ProductResponse result = productService.deleteProduct("1");

        assertEquals(response, result);
        verify(productCommandService).deleteProduct("1");
    }

    @Test
    void searchProducts_shouldDelegateToQueryService() {
        SearchResponse<ProductResponse> response =
                new SearchResponse<>("Results found", 1,
                        List.of(ProductResponse.builder().id("1").build()));

        when(productQueryService.searchProducts("phone"))
                .thenReturn(response);

        SearchResponse<ProductResponse> result =
                productService.searchProducts("phone");

        assertEquals(response, result);
        verify(productQueryService).searchProducts("phone");
    }

    @Test
    void reduceStock_shouldDelegateToStockService() {
        productService.reduceStock("1", 5);

        verify(productStockService).reduceStock("1", 5);
    }

    @Test
    void restoreStock_shouldDelegateToStockService() {
        productService.restoreStock("1", 3);

        verify(productStockService).restoreStock("1", 3);
    }
}
