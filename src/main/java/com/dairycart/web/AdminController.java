package com.dairycart.web;

import com.dairycart.catalog.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("products", productService.findAvailable("", ""));
        model.addAttribute("productForm", new NewProductForm());
        return "admin";
    }

    @PostMapping("/admin/products")
    public String addProduct(@Valid @ModelAttribute("productForm") NewProductForm productForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("products", productService.findAvailable("", ""));
            return "admin";
        }
        productService.addProduct(productForm.getName(), productForm.getDescription(),
                productForm.getCategory(), productForm.getUnit(), productForm.getPrice(),
                productForm.getStock(), productForm.getImageUrl());
        redirectAttributes.addFlashAttribute("notice", "Product added to the catalog.");
        return "redirect:/admin";
    }
}
