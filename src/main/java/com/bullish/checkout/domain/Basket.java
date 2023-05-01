package com.bullish.checkout.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CompositeType;

@Entity
@Table(name = "basket")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @AttributeOverride(
            name = "product",
            column = @Column(name = "product_id")
    )
    @AttributeOverride(
            name = "quantity",
            column =  @Column(name = "product_quantity")
    )
    @CompositeType(BasketLineItemType.class)
    private BasketLineItem basketLineItem;


    public Long getId() {
        return id;
    }

    public BasketLineItem getBasketLineItem() {
        return basketLineItem;
    }

    public void setBasketLineItem(BasketLineItem basketLineItem) {
        this.basketLineItem = basketLineItem;
    }

    @Override
    public String toString() {
        return "Basket{" +
                "id=" + id +
                ", basketLineItem=" + basketLineItem +
                '}';
    }
}
