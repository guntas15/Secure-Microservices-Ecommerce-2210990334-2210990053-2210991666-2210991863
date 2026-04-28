package com.Ecommerce.order_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class CreateOrderRequest {




    @NotNull
    private String productId;

    @NotNull
    @Min(1)
    private Integer quantity;


}
