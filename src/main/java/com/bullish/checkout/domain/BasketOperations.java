package com.bullish.checkout.domain;

import org.springframework.stereotype.Component;

@Component
public class BasketOperations {

    private final DealRepository dealRepository;

    private final ProductRepository productRepository;

    private final BasketRepository basketRepository;


    public BasketOperations(DealRepository dealRepository, ProductRepository productRepository, BasketRepository basketRepository) {
        this.dealRepository = dealRepository;
        this.productRepository = productRepository;
        this.basketRepository = basketRepository;
    }

    public Basket createBasket(){
        Product product = productRepository.findById(1L).get();

        Basket basket = new Basket();

        BasketLineItem basketLineItem = new BasketLineItem(product, 5);

        basket.setBasketLineItem(basketLineItem);

        return basketRepository.save(basket);
    }
}
