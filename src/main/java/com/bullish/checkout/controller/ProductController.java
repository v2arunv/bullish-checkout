package com.bullish.checkout.controller;

import com.bullish.checkout.Constants;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    Administrative endpoints to add and remove products
 */
@RestController
@RequestMapping(Constants.PRODUCT_BASE_PATH)
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
