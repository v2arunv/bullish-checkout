package com.bullish.checkout.domain;

public class BasketOperations {

    private final DealRepository dealRepository;

    private final ProductRepository productRepository;


    public BasketOperations(DealRepository dealRepository, ProductRepository productRepository) {
        this.dealRepository = dealRepository;
        this.productRepository = productRepository;
    }

}
