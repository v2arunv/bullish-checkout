package com.bullish.checkout.controller;

import com.bullish.checkout.Constants;
import com.bullish.checkout.domain.ProductOperations;
import org.springframework.web.bind.annotation.*;

/*
    Administrative endpoints to add and remove products
 */
@RestController
@RequestMapping(Constants.PRODUCT_BASE_PATH)
public class ProductController {

    private final ProductOperations productOperations;

    public ProductController(ProductOperations productOperations){
        this.productOperations = productOperations;
    }

    @GetMapping("/{id}")
    public ProductResponseV1 getProduct(@PathVariable String id) {
        return ProductResponseV1.from(productOperations.getProduct(Long.parseLong(id)));
    }

    @PostMapping
    public ProductResponseV1 addProduct(@RequestBody AddProductRequestV1 request) {

        return ProductResponseV1.from(
                productOperations.createProduct(request.getName(), request.getPrice())
        );

    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productOperations.deleteProduct(Long.parseLong(id));
    }
}
