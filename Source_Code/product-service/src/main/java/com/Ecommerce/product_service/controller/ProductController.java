package com.Ecommerce.product_service.controller;

import com.Ecommerce.product_service.dto.request.ProductRequest;
import com.Ecommerce.product_service.dto.request.ReduceStockRequest;
import com.Ecommerce.product_service.dto.request.RestoreStockRequest;
import com.Ecommerce.product_service.dto.request.UpdateStockRequest;
import com.Ecommerce.product_service.dto.response.ApiResponse;
import com.Ecommerce.product_service.dto.response.ProductListResponse;
import com.Ecommerce.product_service.dto.response.ProductResponse;
import com.Ecommerce.product_service.dto.response.SearchResponse;
import com.Ecommerce.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PRODUCT')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request)
    {

        ProductResponse product = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "Product added successfully",
                        product
                ));
    }



    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
            @PathVariable String id,
            @Valid @RequestBody UpdateStockRequest request) {

        ProductResponse updated =
                productService.updateStock(id, request.getStock());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Stock updated successfully",
                        updated
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }


    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts() {

        List<ProductResponse> products = productService.getAllProducts();

        ProductListResponse response = new ProductListResponse(
                products.isEmpty() ? "No products found" : "Products fetched successfully",
                products.size(),
                products
        );

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<ApiResponse<Void>> reduceStock(
            @PathVariable String id,
            @Valid @RequestBody ReduceStockRequest request) {

        productService.reduceStock(id, request.getQuantity());

        return ResponseEntity.ok(
                new ApiResponse<>("Stock reduced successfully", null)
        );
    }
    @PutMapping("/{id}/restore-stock")
    public ResponseEntity<ApiResponse<Void>> restoreStock(
            @PathVariable String id,
            @RequestBody RestoreStockRequest request) {

        productService.restoreStock(id, request.getQuantity());

        return ResponseEntity.ok(
                new ApiResponse<>("Stock restored successfully", null)
        );
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_PRODUCT')")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProduct(
            @PathVariable String id) {

        ProductResponse deletedProduct = productService.deleteProduct(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Product deleted successfully",
                        deletedProduct
                )
        );
    }


    @GetMapping("/search")
    public ResponseEntity<SearchResponse<ProductResponse>> searchProducts(
            @RequestParam String keyword) {

        return ResponseEntity.ok(productService.searchProducts(keyword));
    }


}
