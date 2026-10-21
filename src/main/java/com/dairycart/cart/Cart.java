package com.dairycart.cart;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart implements Serializable {
    private final Map<String, Integer> quantities = new LinkedHashMap<>();

    public Map<String, Integer> getQuantities() {
        return quantities;
    }

    public void add(String productId, int quantity) {
        quantities.merge(productId, quantity, Integer::sum);
    }

    public void setQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            quantities.remove(productId);
        } else {
            quantities.put(productId, quantity);
        }
    }

    public void remove(String productId) {
        quantities.remove(productId);
    }

    public void clear() {
        quantities.clear();
    }
}
