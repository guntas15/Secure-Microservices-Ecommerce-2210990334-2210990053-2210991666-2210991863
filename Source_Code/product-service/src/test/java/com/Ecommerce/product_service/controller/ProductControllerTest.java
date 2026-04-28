package com.Ecommerce.product_service.controller;

import com.Ecommerce.product_service.dto.request.*;
import com.Ecommerce.product_service.dto.response.*;
import com.Ecommerce.product_service.security.JwtFilter;
import com.Ecommerce.product_service.security.JwtUtil;
import com.Ecommerce.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    // Security checks
    @MockBean
    private JwtFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void createProduct_returnsCreatedProduct() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id("p1")
                .name("Phone")
                .price(BigDecimal.valueOf(1000))
                .stock(10)
                .build();

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "name": "Phone",
                  "description": "Smartphone",
                  "price": 1000,
                  "stock": 10
                }
                """))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Product added successfully"))
                .andExpect(jsonPath("$.data.name")
                        .value("Phone"));
    }

    @Test
    void updateStock_returnsUpdatedProduct() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id("p1")
                .stock(20)
                .build();

        when(productService.updateStock("p1", 20))
                .thenReturn(response);

        mockMvc.perform(put("/api/products/p1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "stock": 20 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Stock updated successfully"))
                .andExpect(jsonPath("$.data.stock")
                        .value(20));
    }

    @Test
    void getProductById_returnsProduct() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id("p1")
                .name("Laptop")
                .build();

        when(productService.getProductById("p1"))
                .thenReturn(response);

        mockMvc.perform(get("/api/products/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Laptop"));
    }

    @Test
    void getAllProducts_returnsList() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(
                        ProductResponse.builder().id("p1").build(),
                        ProductResponse.builder().id("p2").build()
                ));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.message")
                        .value("Products fetched successfully"));
    }

    @Test
    void getAllProducts_returnsEmptyMessage() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.message")
                        .value("No products found"));
    }

    @Test
    void reduceStock_returnsSuccess() throws Exception {
        mockMvc.perform(put("/api/products/p1/reduce-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "quantity": 2 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Stock reduced successfully"));
    }

    @Test
    void restoreStock_returnsSuccess() throws Exception {
        mockMvc.perform(put("/api/products/p1/restore-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "quantity": 5 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Stock restored successfully"));
    }

    @Test
    void deleteProduct_returnsDeletedProduct() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id("p1")
                .build();

        when(productService.deleteProduct("p1"))
                .thenReturn(response);

        mockMvc.perform(delete("/api/products/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Product deleted successfully"));
    }

    @Test
    void searchProducts_returnsResults() throws Exception {
        SearchResponse<ProductResponse> response =
                new SearchResponse<>("success", 1,
                        List.of(ProductResponse.builder().id("p1").build()));

        when(productService.searchProducts("phone"))
                .thenReturn(response);

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", "phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }
}
