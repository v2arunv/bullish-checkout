package com.bullish.checkout.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "basket_line_item")
public class BasketLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "basket_id")
    private Basket basket;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // TODO: Add deal here

    @Column(name = "quantity")
    private int quantity;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Basket getBasket() {
        return basket;
    }

    private void setBasket(Basket basket) {
        this.basket = basket;
    }

    public Product getProduct() {
        return product;
    }

    private void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    private void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "BasketLineItem{" +
                "id=" + id +
                ", basket=" + basket.getId() +
                ", product=" + product +
                ", quantity=" + quantity +
                '}';
    }

    public static class Builder {
        private final Basket basket;

        private Product product;
        private int quantity;
        public Builder(Basket basket) {
            this.basket = basket;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public BasketLineItem build() {
            BasketLineItem item = new BasketLineItem();
            item.setQuantity(this.quantity);
            item.setProduct(this.product);
            item.setBasket(this.basket);
            return item;
        }
    }

    public static class Updater {
        private final BasketLineItem item;

        private int quantity;
        public Updater(BasketLineItem item) {
            this.item = item;
        }

        public Updater quantity (int quantity) {
            this.quantity = quantity;
            return this;
        }

        public BasketLineItem update() {
            this.item.setQuantity(quantity);
            return this.item;
        }
    }
}

