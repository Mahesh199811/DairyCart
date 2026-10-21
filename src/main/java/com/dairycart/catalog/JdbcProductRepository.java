package com.dairycart.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcProductRepository implements ProductRepository {
    private static final RowMapper<Product> PRODUCT_MAPPER = (rs, rowNum) -> new Product(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getString("unit"),
            rs.getBigDecimal("price"),
            rs.getInt("stock"),
            rs.getString("image_url")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> findAvailable(String search, String category) {
        String normalizedSearch = search == null ? "" : search.trim();
        String normalizedCategory = category == null ? "" : category.trim();
        return jdbcTemplate.query("""
                SELECT id, name, description, category, unit, price, stock, image_url
                FROM products
                WHERE stock > 0
                  AND (? = '' OR LOWER(name) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?))
                  AND (? = '' OR category = ?)
                ORDER BY category, name
                """,
                PRODUCT_MAPPER,
                normalizedSearch,
                "%" + normalizedSearch + "%",
                "%" + normalizedSearch + "%",
                normalizedCategory,
                normalizedCategory);
    }

    @Override
    public Optional<Product> findById(String id) {
        return jdbcTemplate.query("""
                SELECT id, name, description, category, unit, price, stock, image_url
                FROM products WHERE id = ?
                """, PRODUCT_MAPPER, id).stream().findFirst();
    }

    @Override
    public void add(Product product) {
        jdbcTemplate.update("""
                INSERT INTO products (id, name, description, category, unit, price, stock, image_url)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                product.id(), product.name(), product.description(), product.category(), product.unit(),
                product.price(), product.stock(), product.imageUrl());
    }

    @Override
    public boolean reserveStock(String productId, int quantity) {
        return jdbcTemplate.update("""
                UPDATE products SET stock = stock - ?
                WHERE id = ? AND stock >= ?
                """, quantity, productId, quantity) == 1;
    }
}
