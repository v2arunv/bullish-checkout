package com.bullish.checkout;

import org.springframework.http.HttpStatus;

public class ProductNotInBasketException extends BusinessException{
    public ProductNotInBasketException(Long id) {
        super("Product with id " + id + " does not exist in this basket", "PRODUCT_NOT_IN_BASKET", HttpStatus.BAD_REQUEST);
    }
}
