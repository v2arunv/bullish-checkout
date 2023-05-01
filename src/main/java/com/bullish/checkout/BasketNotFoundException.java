package com.bullish.checkout;

import org.springframework.http.HttpStatus;

public class BasketNotFoundException extends BusinessException{
    public BasketNotFoundException(Long id) {
        super("Basket with id " + id + " not found", "BASKET_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
