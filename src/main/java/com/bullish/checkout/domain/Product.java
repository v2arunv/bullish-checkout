package com.bullish.checkout.domain;

import com.bullish.checkout.Constants;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

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

    public Money getPrice() {
        return price;
    }

    private void setPrice(Money price) {
        this.price = price;
    }


    private void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
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

    public static class Builder {

        private String name;
        private Money price;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(BigDecimal amount) {
            /*
                We're going to assume that all products are denominated in HKD
                We can always extend it to something else based on the requirements,
                but for the moment, there are no requirements to support multiple currencies,
                and it is therefore an internal detail that doesn't need to be exposed
            */
            this.price = Money.of(
                    amount,
                    Constants.DEFAULT_CURRENCY
            );
            return this;
        }

        public Product build() {
            Product product =  new Product();
            product.setPrice(price);
            product.setName(name);
            return product;
        }

    }

//    public static class Updater {
//
//        private Product product;
//        public Updater(Product product) {
//            this.product = product;
//        }
//
//        public Updater removeDeal(Deal deal) {
//            product.
//        }
//    }
}
