package com.bullish.checkout.domain;

import org.javamoney.moneta.Money;

import java.io.Serializable;

public class BasketLineItem implements Serializable {

    private static final long serialVersionUID = -5032673345470297050L;

    private final Product product;
    private int quantity;
    public BasketLineItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money calculateFullPrice() {
        return product.getPrice().multiply(quantity);
    }

    @Override
    public String toString() {
        return "BasketLineItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}

