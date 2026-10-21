package com.dairycart.orders;

import java.util.Optional;

import com.dairycart.cart.Cart;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String placeOrder(CheckoutForm form, Cart cart) {
        return orderRepository.create(form, cart);
    }

    public Optional<OrderSummary> findById(String id) {
        return orderRepository.findById(id);
    }
}
