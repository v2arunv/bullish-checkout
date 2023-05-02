package com.bullish.checkout.domain;

import com.bullish.checkout.InvalidBasketUpdateOperation;
import jakarta.persistence.*;

import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "basket")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "basket", targetEntity = BasketLineItem.class, cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
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

    private void setBasketLineItems(Set<BasketLineItem> basketLineItems) {
        this.basketLineItems = basketLineItems;
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


    public static class Updater {
        private Basket basket;

        private BasketLineItem newBasketLineItem;

        private Optional<BasketLineItem> oldBasketLineItem;
        public Updater(Basket basket) {
            this.basket = basket;

        }
        public Updater item(BasketLineItem basketLineItem) {
            this.oldBasketLineItem = basket.getBasketLineItems()
                    .stream().filter(x -> basketLineItem.getProduct().getId().equals(x.getProduct().getId()))
                    .findFirst();
            this.newBasketLineItem = basketLineItem;
            return this;
        }

        public Basket update() {
            if (this.newBasketLineItem == null) {
                throw new InvalidBasketUpdateOperation("The basket cannot be updated unless the prepare functions such as item() have been called");
            } if (oldBasketLineItem.isPresent()) {
                basket.getBasketLineItems().remove(this.oldBasketLineItem.get());
            }
            basket.getBasketLineItems().add(this.newBasketLineItem);
            return basket;
        }
    }
}
