package com.dairycart.orders;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dairycart.cart.Cart;
import com.dairycart.catalog.Product;
import com.dairycart.catalog.ProductRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcOrderRepository implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ProductRepository productRepository;

    public JdbcOrderRepository(JdbcTemplate jdbcTemplate, ProductRepository productRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public String create(CheckoutForm form, Cart cart) {
        if (cart.getQuantities().isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }

        String orderId = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);
        List<OrderSummary.OrderItem> items = new ArrayList<>();
        for (var entry : cart.getQuantities().entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("A product in your cart is no longer available."));
            int quantity = entry.getValue();
            if (quantity <= 0 || !productRepository.reserveStock(product.id(), quantity)) {
                throw new IllegalArgumentException("Not enough stock for " + product.name()
                        + ". Please review your cart and try again.");
            }
            items.add(new OrderSummary.OrderItem(product.name(), product.unit(), quantity, product.price()));
        }

        var total = items.stream()
                .map(OrderSummary.OrderItem::lineTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        jdbcTemplate.update("""
                INSERT INTO orders
                    (id, customer_name, email, phone, delivery_address, total, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, orderId, form.getCustomerName().trim(), form.getEmail().trim(), form.getPhone().trim(),
                form.getDeliveryAddress().trim(), total, "PLACED", Timestamp.valueOf(createdAt));

        for (var item : items) {
            jdbcTemplate.update("""
                    INSERT INTO order_items (id, order_id, product_name, unit, quantity, unit_price)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, UUID.randomUUID().toString(), orderId, item.productName(), item.unit(),
                    item.quantity(), item.unitPrice());
        }
        return orderId;
    }

    @Override
    public Optional<OrderSummary> findById(String id) {
        List<OrderSummary> orders = jdbcTemplate.query("""
                SELECT id, customer_name, email, phone, delivery_address, total, status, created_at
                FROM orders WHERE id = ?
                """, (rs, rowNum) -> new OrderSummary(
                rs.getString("id"),
                rs.getString("customer_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("delivery_address"),
                rs.getBigDecimal("total"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                findItems(rs.getString("id"))), id);
        return orders.stream().findFirst();
    }

    private List<OrderSummary.OrderItem> findItems(String orderId) {
        return jdbcTemplate.query("""
                SELECT product_name, unit, quantity, unit_price
                FROM order_items WHERE order_id = ? ORDER BY id
                """, (rs, rowNum) -> new OrderSummary.OrderItem(
                rs.getString("product_name"),
                rs.getString("unit"),
                rs.getInt("quantity"),
                rs.getBigDecimal("unit_price")), orderId);
    }
}
