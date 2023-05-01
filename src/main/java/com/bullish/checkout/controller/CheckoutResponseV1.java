package com.bullish.checkout.controller;

import com.bullish.checkout.domain.BasketCheckout;

import java.util.List;

class CheckoutResponseV1 {
    private Long id;

    private List<BasketLineItemWithDealDtoV1> items;

    private MoneyDtoV1 totalAmount;

    private MoneyDtoV1 netAmount;

    public CheckoutResponseV1(Long id, List<BasketLineItemWithDealDtoV1> lineItems, MoneyDtoV1 total, MoneyDtoV1 net) {
        this.id = id;
        this.items = lineItems;
        this.totalAmount = total;
        this.netAmount = net;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MoneyDtoV1 getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(MoneyDtoV1 totalAmount) {
        this.totalAmount = totalAmount;
    }

    public MoneyDtoV1 getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(MoneyDtoV1 netAmount) {
        this.netAmount = netAmount;
    }

    public List<BasketLineItemWithDealDtoV1> getItems() {
        return items;
    }

    public void setItems(List<BasketLineItemWithDealDtoV1> items) {
        this.items = items;
    }

    public static CheckoutResponseV1 from(BasketCheckout basketCheckout) {
        List<BasketLineItemWithDealDtoV1> lineItems = basketCheckout.getLineItemWithDeals().stream().map(BasketLineItemWithDealDtoV1::from).toList();

        return new CheckoutResponseV1(
                basketCheckout.getId(),
                lineItems,
                MoneyDtoV1.from(basketCheckout.getTotalAmount()),
                MoneyDtoV1.from(basketCheckout.getNetAmount())
        );
    }
}
