package com.Ecommerce.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deleted_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deletedId;

    private String originalProductId;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private LocalDateTime deletedAt;
}
