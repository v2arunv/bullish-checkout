package com.bullish.checkout.controller;

import com.bullish.checkout.domain.Product;

/*
    Yes, this is almost identical to ProductDtoV1 - but that is only the case for now. We want to make sure these
    classes are single-purpose only. Otherwise, there's a good chance, end up with some tangled web of dependencies
 */

public class ProductResponseV1 {

    private Long id;

    private String name;
    private MoneyDtoV1 price;

    public ProductResponseV1(Long id, String name, MoneyDtoV1 price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MoneyDtoV1 getPrice() {
        return price;
    }

    public void setPrice(MoneyDtoV1 price) {
        this.price = price;
    }

    public static ProductResponseV1 from(Product product) {
        return new ProductResponseV1(
                product.getId(),
                product.getName(),
                MoneyDtoV1.from(product.getPrice())
        );
    }
}
