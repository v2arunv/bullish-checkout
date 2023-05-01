package com.bullish.checkout.domain;

import com.bullish.checkout.Constants;
import org.javamoney.moneta.Money;

import java.util.List;

public class BasketLineItemWithDeal {

    private final BasketLineItem basketLineItem;

    private final Money originalPrice;

    private List<Deal> eligibleDeals;

    private Money discountedPrice;

    public BasketLineItemWithDeal(BasketLineItem basketLineItem, List<Deal> deals) {
        this.basketLineItem = basketLineItem;
        this.eligibleDeals = deals;
        this.originalPrice = calculateOriginalPrice();
        this.discountedPrice = Money.of(0, Constants.DEFAULT_CURRENCY);
    }

    public BasketLineItem getBasketLineItem() {
        return basketLineItem;
    }

    public Money getOriginalPrice() {
        return originalPrice;
    }

    public List<Deal> getDeals() {
        return eligibleDeals;
    }


    private Money calculateOriginalPrice() {
        return basketLineItem
                .getProduct()
                .getPrice()
                .multiply(basketLineItem.getQuantity());
    }


    public Money getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Money discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    @Override
    public String toString() {
        return "BasketLineItemWithDeal{" +
                "basketLineItem=" + basketLineItem +
                ", originalPrice=" + originalPrice +
                ", eligibleDeals=" + eligibleDeals +
                ", discountedPrice=" + discountedPrice +
                '}';
    }
}
