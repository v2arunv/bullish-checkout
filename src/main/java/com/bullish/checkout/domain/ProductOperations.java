package com.bullish.checkout.domain;

import com.bullish.checkout.BusinessException;
import com.bullish.checkout.ProductNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Supplier;

@Component
public class ProductOperations {

    private final ProductRepository productRepository;

    public ProductOperations(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct() {

        Product product = new Product.Builder()
                .price(BigDecimal.valueOf(99.99))
                .name("VVPods Pro")
                .build();

        return productRepository.save(product);
    }

    public String getProduct() {
        StringBuilder result = new StringBuilder();
        productRepository.findAll()
                .forEach(prod -> {
                    result
                            .append(prod.toString())
                            .append("\n");
                });

        return result.toString();
    }

    public String getProduct(String id) {
        return productRepository.findById(Long.parseLong(id)).get().toString();
    }

    public void deleteProduct(Long id) throws BusinessException {
        Product product = productRepository
                .findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(id));

        productRepository.delete(product);
    }
}
