package com.Ecommerce.order_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {

    private String id;
    private String name;
    private BigDecimal price;
}
