package com.bullish.checkout.domain.dealapplicator;

import com.bullish.checkout.domain.*;

import java.util.List;

public abstract class DealApplicatorFactory {

    protected final DealRepository dealRepository;
    protected final ProductRepository productRepository;

    public abstract StandardDealApplicator createStandardDealApplicator(Basket basket);

    public DealApplicatorFactory(
            DealRepository dealRepository,
            ProductRepository productRepository
    ) {
        this.dealRepository = dealRepository;
        this.productRepository = productRepository;
    }

    // Common logic for finding relevant deals for a given product that can be shared across
    // all concrete implementations of a deal applicator factory
    protected BasketLineItemWithDeal createLineItemWithDeal(BasketLineItem basketLineItem) {
        List<Deal> eligibleDeals = dealRepository.findAllByProduct(basketLineItem.getProduct());
        return new BasketLineItemWithDeal(basketLineItem, eligibleDeals);
    }

}
