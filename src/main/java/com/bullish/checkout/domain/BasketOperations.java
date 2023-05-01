package com.bullish.checkout.domain;

import com.bullish.checkout.domain.dealapplicator.DealApplicatorFactory;
import org.springframework.stereotype.Component;

@Component
public class BasketOperations {

    private final DealRepository dealRepository;

    private final ProductRepository productRepository;


    // TODO: See if these two can be abstracted away into a higher-order repository
    private final BasketRepository basketRepository;
    private final BasketLineItemRepository basketLineItemRepository;

    private final DealApplicatorFactory dealApplicatorFactory;

    public BasketOperations(
            DealRepository dealRepository,
            ProductRepository productRepository,
            BasketRepository basketRepository,
            BasketLineItemRepository basketLineItemRepository,
            DealApplicatorFactory dealApplicatorFactory
    ) {
        this.dealRepository = dealRepository;
        this.productRepository = productRepository;
        this.basketRepository = basketRepository;
        this.basketLineItemRepository = basketLineItemRepository;
        this.dealApplicatorFactory = dealApplicatorFactory;
    }

    public Basket createBasket(){
        Basket basket = new Basket();

        basketRepository.save(basket);

        return basket;
    }

    public BasketLineItem addToBasket() {
        Basket basket = basketRepository.findById(1L).get();

        Product product = productRepository.findById(1L).get();

        BasketLineItem basketLineItem = new BasketLineItem();
        basketLineItem.setBasket(basket);
        basketLineItem.setProduct(product);
        basketLineItem.setQuantity(5);

        return basketLineItemRepository.save(basketLineItem);
    }

    public Basket getBasket(String id) {
        Basket basket = basketRepository.findById(Long.valueOf(id)).get();

        return basket;
    }

    public BasketCheckout checkout(String id) {
        Basket basket = basketRepository.findById(Long.valueOf(id)).get();

        return dealApplicatorFactory
                .getStandardDealApplicator(basket)
                .calculate();
    }
}
