package com.bullish.checkout;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found", "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
