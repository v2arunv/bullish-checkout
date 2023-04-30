package com.bullish.checkout;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class BusinessException extends ResponseStatusException {
    private String message;
    private String id;
    private HttpStatusCode code;
    public BusinessException(String message, String id, HttpStatusCode code) {
        super(code, message, null, id, null);

    }
}
