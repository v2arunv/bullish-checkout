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

    @GetMapping("/checkout")
    public String performCheckout() {
        return "performCheckout";
    }

    @PostMapping
    public String createBasket() {
        return basketOperations.createBasket().toString();
    }

    @PostMapping("/product")
    public String addProduct() {
        return "addProdcut";
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
