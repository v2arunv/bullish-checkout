package com.bullish.checkout.controller;

import com.bullish.checkout.domain.BasketLineItem;

class BasketLineItemDtoV1 {
    public BasketLineItemDtoV1(ProductDtoV1 product, int quantity) {
        this.product = product;
        this.quantity = quantity;
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
    private ProductDtoV1 product;

    private int quantity;

    public static BasketLineItemDtoV1 from(BasketLineItem basketLineItem) {
        return new BasketLineItemDtoV1(
                ProductDtoV1.from(basketLineItem.getProduct()),
                basketLineItem.getQuantity()
        );
    }
}
