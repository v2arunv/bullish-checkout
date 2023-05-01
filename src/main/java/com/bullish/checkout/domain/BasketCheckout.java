package com.bullish.checkout.domain;

import com.bullish.checkout.Constants;
import com.bullish.checkout.domain.BasketLineItemWithDeal;
import org.javamoney.moneta.Money;

import java.util.List;

public class BasketCheckout {

    private Long id;
    private final List<BasketLineItemWithDeal> lineItemWithDeals;

    private Money totalAmount;

    private Money netAmount;


    public BasketCheckout(Long id, List<BasketLineItemWithDeal> lineItemWithDeals) {
        this.id = id;
        this.lineItemWithDeals = lineItemWithDeals;
        this.totalAmount = calculateTotalAmount();
    }

    public List<BasketLineItemWithDeal> getLineItemWithDeals() {
        return lineItemWithDeals;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public Money getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Money netAmount) {
        this.netAmount = netAmount;
    }

    public Long getId() {
        return id;
    }

    public Money calculateTotalAmount() {
        return this.lineItemWithDeals.stream()
                .reduce(
                        Money.of(0, Constants.DEFAULT_CURRENCY),
                        (money, item) -> item.getOriginalPrice(),
                        Money::add
                );
    }

    @Override
    public String toString() {
        return "BasketCheckout{" +
                "lineItemWithDeals=" + lineItemWithDeals +
                ", totalAmount=" + totalAmount +
                ", netAmount=" + netAmount +
                '}';
    }
}
