package com.bullish.checkout.domain.dealapplicator;

import com.bullish.checkout.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DealApplicatorFactory {

    private final DealRepository dealRepository;

    private final ProductRepository productRepository;

    public DealApplicatorFactory(
            DealRepository dealRepository,
            ProductRepository productRepository
    ) {
        this.dealRepository = dealRepository;
        this.productRepository = productRepository;
    }

    public StandardDealApplicator getStandardDeal(Basket basket) {
        List<BasketLineItemWithDeal> lineItems = basket.getBasketLineItems()
                .stream()
                .map(this::createLineItemWithDeal)
                .toList();

        BasketCheckout checkout = new BasketCheckout(lineItems);
        return new StandardDealApplicator(checkout);


    }

    private BasketLineItemWithDeal createLineItemWithDeal(BasketLineItem basketLineItem) {
        List<Deal> eligibleDeals = dealRepository.findAllByProductId(basketLineItem.getProduct().getId());
        return new BasketLineItemWithDeal(basketLineItem, eligibleDeals);
    }


}
