package com.bullish.checkout;


import org.springframework.http.HttpStatus;

public class InvalidDealException extends BusinessException {
    public InvalidDealException(String message) {
        super(message, "INVALID_DEAL", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

