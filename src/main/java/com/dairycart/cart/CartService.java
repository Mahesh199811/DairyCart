package com.dairycart.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.dairycart.catalog.Product;
import com.dairycart.catalog.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<CartLine> lines(Cart cart) {
        return cart.getQuantities().entrySet().stream()
                .map(entry -> productRepository.findById(entry.getKey())
                        .map(product -> new CartLine(product, entry.getValue(),
                                product.price().multiply(BigDecimal.valueOf(entry.getValue()))))
                        .orElse(null))
                .filter(line -> line != null)
                .toList();
    }

    public BigDecimal total(Cart cart) {
        return lines(cart).stream()
                .map(CartLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void add(Cart cart, String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("That product is no longer available."));
        int updatedQuantity = cart.getQuantities().getOrDefault(productId, 0) + quantity;
        if (updatedQuantity > product.stock()) {
            throw new IllegalArgumentException("Only " + product.stock() + " units are currently available.");
        }
        cart.add(productId, quantity);
    }

    public void update(Cart cart, Map<String, Integer> quantities) {
        quantities.forEach((productId, quantity) -> {
            if (quantity < 0 || quantity > 20) {
                throw new IllegalArgumentException("Choose a quantity between 0 and 20.");
            }
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("That product is no longer available."));
            if (quantity > product.stock()) {
                throw new IllegalArgumentException("Only " + product.stock() + " units of "
                        + product.name() + " are currently available.");
            }
        });
        quantities.forEach(cart::setQuantity);
    }
}
