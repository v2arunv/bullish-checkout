package com.bullish.checkout.integration.stubs;

import java.util.Arrays;
import java.util.Objects;

public class ProductStub {

    public ProductStub(Long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long id;
    public String name;
    public int price;

    public static ProductStub[] ALL = new ProductStub[] {
            new ProductStub(1L ,"Macbook Pro", 1299),
            new ProductStub(2L ,"iPhone", 999),
            new ProductStub(3L ,"AirPods Pro", 499)
    };

    public static ProductStub getById(int id) {
        return Arrays.stream(ALL).filter(x -> Objects.equals(x.id, (long) id)).findFirst().get();
    }
    public static ProductStub getById(Long id) {
        return Arrays.stream(ALL).filter(x -> Objects.equals(x.id, id)).findFirst().get();
    }
}
