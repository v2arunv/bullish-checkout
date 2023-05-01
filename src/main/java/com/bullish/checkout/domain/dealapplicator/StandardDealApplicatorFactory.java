package com.bullish.checkout.domain.dealapplicator;

import com.bullish.checkout.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StandardDealApplicatorFactory extends DealApplicatorFactory {

    public StandardDealApplicatorFactory(DealRepository dealRepository, ProductRepository productRepository) {
        super(dealRepository, productRepository);
    }

    @Override
    public StandardDealApplicator createStandardDealApplicator(Basket basket) {
        List<BasketLineItemWithDeal> lineItems = basket.getBasketLineItems()
                .stream()
                .map(this::createLineItemWithDeal)
                .toList();

        BasketCheckout checkout = new BasketCheckout(lineItems);
        return new StandardDealApplicator(checkout);
    }
}
