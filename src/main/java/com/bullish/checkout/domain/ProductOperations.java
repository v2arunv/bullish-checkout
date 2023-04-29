package com.bullish.checkout.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductOperations {

    private final ProductRepository productRepository;

    public ProductOperations(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct() {
        Product product = new Product();
        product.setName("SAMPLE");

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
                            .append(", ");
                });

        return result.toString();
    }
}
