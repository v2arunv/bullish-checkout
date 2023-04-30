package com.bullish.checkout;

import org.springframework.http.HttpStatus;

public class DealNotFoundException extends BusinessException {
    public DealNotFoundException(Long id) {
        super("Deal with id " + id + " not found", "DEAL_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
