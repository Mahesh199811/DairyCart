package com.dairycart.web;

import com.dairycart.cart.Cart;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CartModelAdvice {
    private final Cart cart;

    public CartModelAdvice(Cart cart) {
        this.cart = cart;
    }

    @ModelAttribute
    public void addCartCount(Model model) {
        model.addAttribute("cartCount", cart.getQuantities().values().stream().mapToInt(Integer::intValue).sum());
    }
}
