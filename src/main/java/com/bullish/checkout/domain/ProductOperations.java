package com.bullish.checkout.domain;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.math.BigDecimal;

@Component
public class ProductOperations {

    private final ProductRepository productRepository;

    public ProductOperations(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct() {
        Product product = new Product();
        product.setId(5L);
        product.setName("SAMPLE");
        product.setPrice(Money.of(
                BigDecimal.valueOf(99.99),
                Monetary.getCurrency("USD")
        ));

        productRepository.save(product);
    }

    public String getProduct() {
        StringBuilder result = new StringBuilder();
        productRepository.findAll()
                .forEach(prod -> {
                    result
                            .append(prod.getId())
                            .append("-")
                            .append(prod.getName())
                            .append("-")
                            .append(prod.getPrice().getCurrency().getCurrencyCode())
                            .append(prod.getPrice().getNumberStripped().toString())
                            .append(", ");
                });

        return result.toString();
    }
}
