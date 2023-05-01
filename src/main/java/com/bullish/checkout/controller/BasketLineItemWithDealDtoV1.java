package com.bullish.checkout.controller;

import com.bullish.checkout.domain.BasketLineItemWithDeal;

public class BasketLineItemWithDealDtoV1 {

    private ProductDtoV1 product;

    private int quantity;

    private MoneyDtoV1 totalAmount;

    private MoneyDtoV1 netAmount;

    public BasketLineItemWithDealDtoV1(ProductDtoV1 product, int quantity, MoneyDtoV1 total, MoneyDtoV1 net) {
        this.product = product;
        this.quantity = quantity;
        this.totalAmount = total;
        this.netAmount = net;
    }


    public ProductDtoV1 getProduct() {
        return product;
    }

    public void setProduct(ProductDtoV1 product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public static BasketLineItemWithDealDtoV1 from(BasketLineItemWithDeal basketLineItemWithDeal) {
        return new BasketLineItemWithDealDtoV1(
                ProductDtoV1.from(basketLineItemWithDeal.getBasketLineItem().getProduct()),
                basketLineItemWithDeal.getBasketLineItem().getQuantity(),
                MoneyDtoV1.from(basketLineItemWithDeal.getOriginalPrice()),
                MoneyDtoV1.from(basketLineItemWithDeal.getDiscountedPrice())
        );
    }
}
