package com.dairycart.catalog;

import java.math.BigDecimal;

public record Product(
        String id,
        String name,
        String description,
        String category,
        String unit,
        BigDecimal price,
        int stock,
        String imageUrl
) {
}
