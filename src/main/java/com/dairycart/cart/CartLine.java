package com.dairycart.cart;

import java.math.BigDecimal;

import com.dairycart.catalog.Product;

public record CartLine(Product product, int quantity, BigDecimal lineTotal) {
}
