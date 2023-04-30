package com.bullish.checkout.domain;

import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DealOperations {

    private final DealRepository dealRepository;

    private final ProductRepository productRepository;

    public DealOperations(DealRepository dealRepository, ProductRepository productRepository) {
        this.dealRepository = dealRepository;
        this.productRepository = productRepository;
    }

    public Deal createDeal() {
        Product product = productRepository.findById(1L).orElseThrow();

        Deal deal = new Deal();
        deal.setType(DealType.PERCENTAGE);
        deal.setDiscountPercentage(BigDecimal.valueOf(50));
        deal.setMinimumQuantity(2L);
        deal.setProduct(product);
        deal.setFlatDiscount(
                Money.of(BigDecimal.valueOf(100), "USD")
        );

        return dealRepository.save(deal);

    }

    public String getById(String id) {
        return dealRepository.findById(Long.getLong(id)).get().toString();
    }

    public String getByProduct(String productId) {
        return dealRepository.findAllByProductId(Long.parseLong(productId)).toString();
    }
}
