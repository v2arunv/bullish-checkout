package com.bullish.checkout.controller;

class PatchProductInBasketV1 {

    private Long productId;
    private int quantity;

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
