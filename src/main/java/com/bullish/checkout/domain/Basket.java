package com.bullish.checkout.domain;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "basket")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "basket")
    private Set<BasketLineItem> basketLineItems;

    private Basket() {
        this.basketLineItems = Set.of();
    }

    public Long getId() {
        return id;
    }

    public Set<BasketLineItem> getBasketLineItems() {
        return basketLineItems;
    }

    @Override
    public String toString() {
        return "Basket{" +
                "id=" + id +
                ", basketLineItems=" + basketLineItems +
                '}';
    }

    public static class Builder {

        private static Set<BasketLineItem> basketLineItems;

        private final Basket basket;

        public Builder() {
            this.basket = new Basket();
        }

        public Basket build() {
            return this.basket;
        }

    }


}
