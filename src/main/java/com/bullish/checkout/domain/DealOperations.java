package com.bullish.checkout.domain;

import com.bullish.checkout.BasketNotFoundException;
import com.bullish.checkout.BusinessException;
import com.bullish.checkout.DealNotFoundException;
import com.bullish.checkout.ProductNotFoundException;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

@Component
public class DealOperations {

    private final DealRepository dealRepository;

    private final ProductRepository productRepository;

    public DealOperations(DealRepository dealRepository, ProductRepository productRepository) {
        this.dealRepository = dealRepository;
        this.productRepository = productRepository;
    }


    public Deal createFlatDiscountDeal(Long productId, BigDecimal flatDiscount, Long minimumQuantity, Long maximumQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(productId));

        Deal deal = new Deal.Builder(product, DealType.FLAT_AMOUNT)
                .flatDiscount(flatDiscount)
                .minimumQuantity(minimumQuantity)
                .maximumQuantity(maximumQuantity)
                .build();

        dealRepository.save(deal);

        return deal;

    }

    public Deal createPercentageDiscountDeal(Long productId, BigDecimal discountPercentage, Long minimumQuantity, Long maximumQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(productId));

        Deal deal = new Deal.Builder(product, DealType.PERCENTAGE)
                .discountPercentage(discountPercentage)
                .minimumQuantity(minimumQuantity)
                .maximumQuantity(maximumQuantity)
                .build();

        dealRepository.save(deal);

        return deal;
    }

    public Deal getById(Long id) {
        return dealRepository.findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new DealNotFoundException(id));
    }

    public List<Deal> getByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(productId));
        return dealRepository.findAllByProduct(product);
    }

    public void deleteDeal(Long id) throws BusinessException {
        Deal deal = dealRepository
                .findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new DealNotFoundException(id));

        dealRepository.delete(deal);
    }
}
