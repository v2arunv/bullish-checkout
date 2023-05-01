package com.bullish.checkout.controller;


import com.bullish.checkout.Constants;
import com.bullish.checkout.domain.BasketOperations;
import com.bullish.checkout.domain.ProductOperations;
import org.springframework.web.bind.annotation.*;

/*
    Represents a user's basket. Products can be added and removed. A total price can be calculated based on items in the basket
 */
@RestController
@RequestMapping(Constants.BASKET_BASE_PATH)
public class BasketController {

    private final BasketOperations basketOperations;

    public BasketController(BasketOperations basketOperations) {
        this.basketOperations = basketOperations;
    }

    @GetMapping("/{id}")
    public String getBasket(@PathVariable String id) {
        return basketOperations.getBasket(id).toString();
    }

    @PostMapping("/checkout/{id}")
    public String performCheckout(@PathVariable String id) {
        return basketOperations.checkout(id).toString();
    }

    @PostMapping
    public String createBasket() {
        return basketOperations.createBasket().toString();
    }

    @PostMapping("/product")
    public String addProduct() {
        return basketOperations.addToBasket().toString();
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
