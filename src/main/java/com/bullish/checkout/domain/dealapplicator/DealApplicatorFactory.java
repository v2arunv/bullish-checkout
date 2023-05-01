package com.bullish.checkout.domain.dealapplicator;

import com.bullish.checkout.domain.Basket;
import com.bullish.checkout.domain.DealRepository;
import com.bullish.checkout.domain.ProductRepository;

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



}
