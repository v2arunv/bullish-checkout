package com.bullish.checkout.controller;

import com.bullish.checkout.domain.Basket;

import java.util.Set;
import java.util.stream.Collectors;


class BasketResponseV1 {
    private Long id;

    private Set<BasketLineItemDtoV1> items;
    public BasketResponseV1(Long id, Set<BasketLineItemDtoV1> items) {
        this.id = id;
        this.items = items;
    }

    public static BasketResponseV1 from(Basket basket) {
        Set<BasketLineItemDtoV1> items = basket.getBasketLineItems()
                .stream().map(BasketLineItemDtoV1::from).collect(Collectors.toSet());

        return new BasketResponseV1(
                basket.getId(),
                items
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<BasketLineItemDtoV1> getItems() {
        return items;
    }

    public void setItems(Set<BasketLineItemDtoV1> items) {
        this.items = items;
    }
}
