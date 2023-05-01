package com.bullish.checkout;

import org.springframework.http.HttpStatus;

public class InvalidQuantityArgumentException extends BusinessException {
    public InvalidQuantityArgumentException(String message) {
        super(message, "INVALID_PRODUCT_QUANTITY", HttpStatus.BAD_REQUEST);
    }
}
