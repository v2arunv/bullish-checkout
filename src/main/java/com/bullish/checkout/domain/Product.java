package com.bullish.checkout.domain;

import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;

import java.util.Set;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    @AttributeOverride(
            name = "amount",
            column = @Column(name = "price_amount")
    )
    @AttributeOverride(
            name="currency",
            column =  @Column(name = "price_currency")
    )
    @CompositeType(MonetaryAmountType.class)
    private Money price;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
