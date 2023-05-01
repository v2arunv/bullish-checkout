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

    public void setId(Long id) {
        this.id = id;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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
}

