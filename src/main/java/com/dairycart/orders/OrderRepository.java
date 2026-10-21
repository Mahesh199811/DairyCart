package com.dairycart.orders;

import java.util.Optional;

import com.dairycart.cart.Cart;

public interface OrderRepository {
    String create(CheckoutForm form, Cart cart);

    Optional<OrderSummary> findById(String id);
}
