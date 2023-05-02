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

    public Product createProduct(String name, double price) {

        Product product = new Product.Builder()
                .price(BigDecimal.valueOf(price))
                .name(name)
                .build();

        return productRepository.save(product);
    }


    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(id));
    }

    public void deleteProduct(Long id) throws BusinessException {
        Product product = productRepository
                .findById(id)
                .orElseThrow((Supplier<BusinessException>) () -> new ProductNotFoundException(id));

        productRepository.delete(product);
    }
}
