package com.dairycart.web;

import java.util.Map;

import com.dairycart.cart.Cart;
import com.dairycart.cart.CartService;
import com.dairycart.catalog.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {
    private final Cart cart;
    private final CartService cartService;
    private final ProductService productService;

    public CartController(Cart cart, CartService cartService, ProductService productService) {
        this.cart = cart;
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/cart/add/{productId}")
    public String add(@PathVariable String productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      RedirectAttributes redirectAttributes) {
        try {
            if (quantity < 1 || quantity > 20) {
                throw new IllegalArgumentException("Choose a quantity between 1 and 20.");
            }
            cartService.add(cart, productId, quantity);
            redirectAttributes.addFlashAttribute("notice", "Added to your basket.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String view(Model model) {
        model.addAttribute("lines", cartService.lines(cart));
        model.addAttribute("total", cartService.total(cart));
        return "cart";
    }

    @PostMapping("/cart/update")
    public String update(@RequestParam Map<String, String> parameters,
                         RedirectAttributes redirectAttributes) {
        try {
            Map<String, Integer> quantities = parameters.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("quantity_"))
                    .collect(java.util.stream.Collectors.toMap(
                            entry -> entry.getKey().substring("quantity_".length()),
                            entry -> Integer.parseInt(entry.getValue())));
            cartService.update(cart, quantities);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", "Please check the quantities in your basket.");
        }
        return "redirect:/cart";
    }
}
