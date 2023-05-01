package com.bullish.checkout.controller;

class RemoveProductFromBasketRequestV1 {

    private Long basketId;

    private Long productId;

    public Long getBasketId() {
        return basketId;
    }

    public void setBasketId(Long basketId) {
        this.basketId = basketId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
