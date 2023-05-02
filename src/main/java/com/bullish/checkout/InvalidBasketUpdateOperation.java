package com.bullish.checkout;

import org.springframework.http.HttpStatus;

public class InvalidBasketUpdateOperation extends BusinessException {
    public InvalidBasketUpdateOperation(String message) {
        super(message, "INVALID_UPDATE_ON_BASKET", HttpStatus.BAD_REQUEST);

    }
}
