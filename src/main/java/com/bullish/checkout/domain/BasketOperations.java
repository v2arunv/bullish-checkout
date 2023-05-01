package com.bullish.checkout.domain;

import com.bullish.checkout.BusinessException;
import com.bullish.checkout.ProductNotFoundException;
import com.bullish.checkout.domain.dealapplicator.StandardDealApplicatorFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class BasketOperations {

    private final DealRepository dealRepository;

    private final ProductRepository productRepository;


    // TODO: See if these two can be abstracted away into a higher-order repository
    private final BasketRepository basketRepository;
    private final BasketLineItemRepository basketLineItemRepository;

    private final StandardDealApplicatorFactory dealApplicatorFactory;

    public BasketOperations(
            DealRepository dealRepository,
            ProductRepository productRepository,
            BasketRepository basketRepository,
            BasketLineItemRepository basketLineItemRepository,
            StandardDealApplicatorFactory dealApplicatorFactory
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

    public Basket addToBasket(Long basketId, Long productId, int quantity) {
        Basket basket = basketRepository.findById(basketId).get();

        Product product = productRepository.findById(productId).get();

        BasketLineItem basketLineItem = new BasketLineItem();
        basketLineItem.setBasket(basket);
        basketLineItem.setProduct(product);
        basketLineItem.setQuantity(quantity);

        basketLineItemRepository.save(basketLineItem);
        return basket;
    }

    public Basket getBasket(Long id) {
        Basket basket = basketRepository.findById(id).get();

        return basket;
    }

    public BasketCheckout checkout(Long id) {
        Basket basket = basketRepository.findById(id).get();

        return dealApplicatorFactory
                .createStandardDealApplicator(basket)
                .calculate();
    }

    public void deleteBasket(Long id) {
        basketRepository.deleteById(id);
    }

    public Basket patchProductInBasket(Long basketId, Long productId, int quantity) {
        Basket basket = basketRepository.findById(basketId).get();

        BasketLineItem item =  basket.getBasketLineItems()
                .stream()
                .filter(x -> x.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(productId));

        if (quantity == 0) {
            basketLineItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            basketLineItemRepository.save(item);
        }
        return basket;
    }
}
