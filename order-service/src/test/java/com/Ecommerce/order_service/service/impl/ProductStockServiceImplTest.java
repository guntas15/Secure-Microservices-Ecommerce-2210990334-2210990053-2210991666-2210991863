package com.Ecommerce.order_service.service.impl;

import com.Ecommerce.order_service.client.ProductClient;
import com.Ecommerce.order_service.dto.request.ReduceStockRequest;
import com.Ecommerce.order_service.dto.request.RestoreStockRequest;
import com.Ecommerce.order_service.dto.response.ProductResponse;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductStockServiceImplTest {

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private ProductStockServiceImpl productStockService;

    // 4 test cases

    @BeforeEach
    void startingMethod() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProduct_returnsProduct() {

        ProductResponse product = ProductResponse.builder()
                .id("prod-1")
                .price(BigDecimal.valueOf(100))
                .build();

        when(productClient.getProductById("prod-1", "token"))
                .thenReturn(product);

        ProductResponse response =
                productStockService.getProduct("prod-1", "token");

        assertEquals("prod-1", response.getId());
    }

    @Test
    void reduceStock_reducesStockSuccessfully() {

        doNothing().when(productClient).reduceStock(
                eq("prod-1"),
                any(ReduceStockRequest.class),
                eq("token")
        );

        assertDoesNotThrow(() ->
                productStockService.reduceStock("prod-1", 2, "token")
        );

        verify(productClient).reduceStock(
                eq("prod-1"),
                any(ReduceStockRequest.class),
                eq("token")
        );
    }

    @Test
    void reduceStock_throwsIllegalStateException_whenStockInsufficient() {

        FeignException.BadRequest feignException =
                new FeignException.BadRequest(
                        "Bad Request",
                        Request.create(
                                Request.HttpMethod.PUT,
                                "/api/products/prod-1/reduce-stock",
                                Collections.emptyMap(),
                                null,
                                new RequestTemplate()
                        ),
                        null,
                        null
                );

        doThrow(feignException).when(productClient).reduceStock(
                eq("prod-1"),
                any(ReduceStockRequest.class),
                eq("token")
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> productStockService.reduceStock("prod-1", 5, "token")
                );

        assertEquals(
                "Cannot place order: insufficient stock",
                exception.getMessage()
        );
    }

    @Test
    void restoreStock_restoresStockSuccessfully() {

        doNothing().when(productClient).restoreStock(
                eq("prod-1"),
                any(RestoreStockRequest.class),
                eq("token")
        );

        assertDoesNotThrow(() ->
                productStockService.restoreStock("prod-1", 2, "token")
        );

        verify(productClient).restoreStock(
                eq("prod-1"),
                any(RestoreStockRequest.class),
                eq("token")
        );
    }
}
