package com.dairycart.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderSummary(
        String id,
        String customerName,
        String email,
        String phone,
        String deliveryAddress,
        BigDecimal total,
        String status,
        LocalDateTime createdAt,
        List<OrderItem> items
) {
    public record OrderItem(String productName, String unit, int quantity, BigDecimal unitPrice) {
        public BigDecimal lineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
