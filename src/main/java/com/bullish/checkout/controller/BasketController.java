package com.bullish.checkout.controller;


import com.bullish.checkout.Constants;
import com.bullish.checkout.domain.ProductOperations;
import org.springframework.web.bind.annotation.*;

/*
    Represents a user's basket. Products can be added and removed. A total price can be calculated based on items in the basket
 */
@RestController
@RequestMapping(Constants.BASKET_BASE_PATH)
public class BasketController {

    private final ProductOperations productOperations;

    public BasketController(ProductOperations productOperations) {
        this.productOperations = productOperations;
    }

    @GetMapping("/checkout")
    public String performCheckout() {
        return "performCheckout";
    }

    @PostMapping("/product")
    public String addProduct() {
        productOperations.createProduct();
        return productOperations.getProduct();

    }

    @DeleteMapping("/product")
    public String removeProduct() {
        return "removeProduct";
    }

    @DeleteMapping("/")
    public String deleteBasket() {
        return "deleteBasket";
    }


}
