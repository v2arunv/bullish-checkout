package com.bullish.checkout.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    Administrative endpoints to add and remove products
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @PostMapping
    public String addProduct() {
        return "addProduct";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "deleteProduct";
    }
}
