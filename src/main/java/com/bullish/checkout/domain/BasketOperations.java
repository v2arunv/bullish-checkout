package com.bullish.checkout.domain;

import com.bullish.checkout.BasketNotFoundException;
import com.bullish.checkout.BusinessException;
import com.bullish.checkout.ProductNotFoundException;
import com.bullish.checkout.ProductNotInBasketException;
import com.bullish.checkout.domain.dealapplicator.StandardDealApplicatorFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class BasketOperations {
    private final ProductRepository productRepository;


    // TODO: See if these two can be abstracted away into a higher-order repository
    private final BasketRepository basketRepository;
    private final BasketLineItemRepository basketLineItemRepository;

    private final StandardDealApplicatorFactory dealApplicatorFactory;

    public BasketOperations(
            ProductRepository productRepository,
            BasketRepository basketRepository,
            BasketLineItemRepository basketLineItemRepository,
            StandardDealApplicatorFactory dealApplicatorFactory
    ) {
        this.productRepository = productRepository;
        this.basketRepository = basketRepository;
        this.basketLineItemRepository = basketLineItemRepository;
        this.dealApplicatorFactory = dealApplicatorFactory;
    }

    public Basket createBasket() {
        Basket basket = new Basket.Builder().build();

        basketRepository.save(basket);

        return basket;
    }

    public Basket addToBasket(Long basketId, Long productId, int quantity) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow((Supplier<BusinessException>) () -> new BasketNotFoundException(basketId));

        Product product = productRepository.findById(productId)
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(productId));

        BasketLineItem basketLineItem = new BasketLineItem.Builder(basket)
                .product(product)
                .quantity(quantity)
                .build();

        basketLineItemRepository.save(basketLineItem);
        return basket;
    }

    public Basket getBasket(Long id) {
        Basket basket = basketRepository.findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new BasketNotFoundException(id));


        return basket;
    }

    public BasketCheckout checkout(Long id) {
        Basket basket = basketRepository.findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new BasketNotFoundException(id));

        return dealApplicatorFactory
                .createStandardDealApplicator(basket)
                .calculate();
    }

    public void deleteBasket(Long id) {
        Basket basket = basketRepository.findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new BasketNotFoundException(id));

        basketRepository.delete(basket);

    }

    public Basket patchProductInBasket(Long basketId, Long productId, int quantity) {
        Basket basket = basketRepository.findById(basketId).get();

        BasketLineItem item = basket.getBasketLineItems()
                .stream()
                .filter(x -> x.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotInBasketException(productId));

        if (quantity == 0) {
            basketLineItemRepository.delete(item);
        } else {
            BasketLineItem updatedItem = new BasketLineItem.Updater(item)
                    .quantity(quantity)
                    .update();
            basketLineItemRepository.save(updatedItem);
        }
        return basket;
    }
}
