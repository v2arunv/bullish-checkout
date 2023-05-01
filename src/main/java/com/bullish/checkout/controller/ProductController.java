package com.bullish.checkout.controller;

import com.bullish.checkout.Constants;
import com.bullish.checkout.domain.ProductOperations;
import org.springframework.web.bind.annotation.*;

/*
    Administrative endpoints to add and remove products
 */
@RestController
@RequestMapping(Constants.PRODUCT_BASE_PATH)
public class ProductController {

    private final ProductOperations productOperations;

    public ProductController(ProductOperations productOperations){
        this.productOperations = productOperations;
    }

    @GetMapping("/{id}")
    public String getProduct(@PathVariable String id) {
        return productOperations.getProduct(id);
    }

    @PostMapping
    public String addProduct() {
        return productOperations.createProduct().toString();

    }

    @DeleteMapping
    public void deleteProduct() {
        productOperations.deleteProduct(1L);
    }
}
