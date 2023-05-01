package com.bullish.checkout.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CompositeType;

import java.util.Set;

@Entity
@Table(name = "basket")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "basket")
    private Set<BasketLineItem> basketLineItems;

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
}
