package com.bullish.checkout;

import org.springframework.http.HttpStatus;

public class ProductIdQueryParameterMissingException extends BusinessException {
    public ProductIdQueryParameterMissingException() {
        super("Product ID must be passed as a query parameter", "PRODUCT_QUERY_MISSING", HttpStatus.BAD_REQUEST);

    }
}
