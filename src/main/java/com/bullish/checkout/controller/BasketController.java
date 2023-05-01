package com.bullish.checkout.controller;


import com.bullish.checkout.Constants;
import com.bullish.checkout.domain.BasketOperations;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BasketResponseV1> getBasket(@PathVariable String id) {
        return ResponseEntity.ok(BasketResponseV1.from(basketOperations.getBasket(Long.valueOf(id))));
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<BasketResponseV1> createBasket() {
        return ResponseEntity.ok(BasketResponseV1.from(basketOperations.createBasket()));
    }

    @PostMapping("/product")
    public ResponseEntity<BasketResponseV1> addProduct(@RequestBody AddProductToBasketRequestV1 request) {
        return ResponseEntity.ok(BasketResponseV1.from(
                basketOperations.addToBasket(request.getBasketId(), request.getProductId(), request.getQuantity())
        ));
    }

    @DeleteMapping("/product")
    public ResponseEntity<BasketResponseV1> removeProduct(@RequestBody RemoveProductFromBasketRequestV1 request) {
        return ResponseEntity.ok(BasketResponseV1.from(
            basketOperations.patchProductInBasket(request.getBasketId(), request.getProductId(), 0)
        ));
    }

    @PatchMapping("/product")
    public ResponseEntity<BasketResponseV1> updateProduct(@RequestBody PatchProductInBasketV1 request) {
        return ResponseEntity.ok(BasketResponseV1.from(
                basketOperations.patchProductInBasket(request.getBasketId(), request.getProductId(), request.getQuantity())
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBasket(@PathVariable String id) {
        basketOperations.deleteBasket(Long.valueOf(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<CheckoutResponseV1> performCheckout(@PathVariable String id) {
        return ResponseEntity.ok(CheckoutResponseV1.from(basketOperations.checkout(Long.valueOf(id))));
    }

}
