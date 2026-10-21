package com.dairycart.catalog;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAvailable(String search, String category);

    Optional<Product> findById(String id);

    void add(Product product);

    boolean reserveStock(String productId, int quantity);
}
