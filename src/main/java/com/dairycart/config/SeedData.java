package com.dairycart.config;

import com.dairycart.catalog.ProductService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedData {
    @Bean
    ApplicationRunner seedCatalog(ProductService productService) {
        return args -> productService.seedProducts();
    }
}
