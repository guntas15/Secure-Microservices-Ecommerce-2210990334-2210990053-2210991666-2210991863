package com.Ecommerce.order_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    private String productId;

    private Integer quantity;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    //   DOMAIN METHODS

    public void markPaid() {
        if (this.status != OrderStatus.ORDER_PLACED) {
            throw new IllegalStateException(
                    "Only ORDER_PLACED orders can be marked as PAID"
            );
        }
        this.status = OrderStatus.PAID;
    }

    public void markPaymentFailed() {
        if (this.status != OrderStatus.ORDER_PLACED) {
            throw new IllegalStateException(
                    "Only ORDER_PLACED orders can fail payment"
            );
        }
        this.status = OrderStatus.PAYMENT_FAILED;
    }


    public void cancel() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Paid orders cannot be cancelled"
            );
        }
        this.status = OrderStatus.CANCELLED;
    }
    public void markRefunded() {
        this.status = OrderStatus.REFUNDED;
    }
}
