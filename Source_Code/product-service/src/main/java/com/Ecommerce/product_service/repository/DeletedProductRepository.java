package com.Ecommerce.product_service.repository;

import com.Ecommerce.product_service.entity.DeletedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedProductRepository
        extends JpaRepository<DeletedProduct, Long> {
}
