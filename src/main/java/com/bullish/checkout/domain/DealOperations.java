package com.bullish.checkout.domain;

import com.bullish.checkout.BusinessException;
import com.bullish.checkout.ProductNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Supplier;

@Component
public class DealOperations {

    private final DealRepository dealRepository;

    private final ProductRepository productRepository;

    public DealOperations(DealRepository dealRepository, ProductRepository productRepository) {
        this.dealRepository = dealRepository;
        this.productRepository = productRepository;
    }

    public Deal createDeal() throws BusinessException {
        Product product = productRepository.findById(1L).orElseThrow();

        Deal deal = new Deal.Builder(product, DealType.PERCENTAGE)
                .discountPercentage(BigDecimal.valueOf(50))
                .minimumQuantity(2L)
                .build();

        return dealRepository.save(deal);

    }

    public String getById(String id) {
        return dealRepository.findById(Long.getLong(id)).get().toString();
    }

    public String getByProduct(String productId) {
        Product product = productRepository.findById(Long.parseLong(productId)).get();
        return dealRepository.findAllByProduct(product).toString();
    }

    public void deleteDeal(Long id) throws BusinessException {
        Deal deal = dealRepository
                .findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(id));

        dealRepository.delete(deal);
    }
}
