package com.dairycart.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAvailable(String search, String category) {
        return productRepository.findAvailable(search, category);
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public void addProduct(String name, String description, String category, String unit,
                           BigDecimal price, int stock, String imageUrl) {
        productRepository.add(new Product(
                UUID.randomUUID().toString(), name.trim(), description.trim(), category.trim(),
                unit.trim(), price, stock, imageUrl.trim()));
    }

    public void seedProducts() {
        if (productRepository.findById("milk-full-1l").isPresent()) {
            return;
        }
        productRepository.add(new Product("milk-full-1l", "Farm Fresh Milk", "Creamy whole milk, delivered chilled.", "Milk", "1 litre", new BigDecimal("68.00"), 40, "🥛"));
        productRepository.add(new Product("ghee-pure-500g", "Pure Cow Ghee", "Slow-cooked ghee with a rich, nutty aroma.", "Ghee", "500 g", new BigDecimal("420.00"), 18, "🧈"));
        productRepository.add(new Product("paneer-fresh-200g", "Fresh Paneer", "Soft, fresh paneer made in small batches.", "Paneer", "200 g", new BigDecimal("95.00"), 24, "🧀"));
        productRepository.add(new Product("curd-homestyle-500g", "Homestyle Curd", "Thick and gently set curd with a mild tang.", "Curd", "500 g", new BigDecimal("55.00"), 30, "🥣"));
        productRepository.add(new Product("butter-cultured-200g", "Cultured Butter", "Rich cultured butter for toast and cooking.", "Butter", "200 g", new BigDecimal("115.00"), 16, "🧈"));
        productRepository.add(new Product("lassi-sweet-250ml", "Sweet Lassi", "A cool, lightly sweetened yogurt drink.", "Drinks", "250 ml", new BigDecimal("40.00"), 20, "🥤"));
    }
}
