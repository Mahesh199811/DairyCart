package com.dairycart.web;

import com.dairycart.catalog.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StoreController {
    private final ProductService productService;

    public StoreController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String category,
                       Model model) {
        model.addAttribute("products", productService.findAvailable(q, category));
        model.addAttribute("search", q == null ? "" : q);
        model.addAttribute("selectedCategory", category == null ? "" : category);
        model.addAttribute("categories", new String[]{"Milk", "Ghee", "Paneer", "Curd", "Butter", "Drinks"});
        return "index";
    }
}
