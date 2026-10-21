package com.dairycart.web;

import com.dairycart.cart.Cart;
import com.dairycart.cart.CartService;
import com.dairycart.orders.CheckoutForm;
import com.dairycart.orders.OrderService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {
    private final Cart cart;
    private final CartService cartService;
    private final OrderService orderService;

    public CheckoutController(Cart cart, CartService cartService, OrderService orderService) {
        this.cart = cart;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkout(Model model, RedirectAttributes redirectAttributes) {
        if (cart.getQuantities().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Add something to your basket before checkout.");
            return "redirect:/cart";
        }
        model.addAttribute("lines", cartService.lines(cart));
        model.addAttribute("total", cartService.total(cart));
        model.addAttribute("checkoutForm", new CheckoutForm());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@Valid @ModelAttribute CheckoutForm checkoutForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (cart.getQuantities().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your basket is empty.");
            return "redirect:/cart";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("lines", cartService.lines(cart));
            model.addAttribute("total", cartService.total(cart));
            return "checkout";
        }
        try {
            String orderId = orderService.placeOrder(checkoutForm, cart);
            cart.clear();
            return "redirect:/orders/" + orderId;
        } catch (IllegalArgumentException exception) {
            model.addAttribute("lines", cartService.lines(cart));
            model.addAttribute("total", cartService.total(cart));
            model.addAttribute("error", exception.getMessage());
            return "checkout";
        }
    }

    @GetMapping("/orders/{id}")
    public String order(@PathVariable String id, Model model) {
        return orderService.findById(id)
                .map(order -> {
                    model.addAttribute("order", order);
                    return "order-confirmation";
                })
                .orElse("order-not-found");
    }
}
