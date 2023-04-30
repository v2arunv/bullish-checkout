package com.bullish.checkout.controller;
import com.bullish.checkout.Constants;
import com.bullish.checkout.domain.DealOperations;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/*
    Administrative endpoints to add and remove deals
 */
@RestController
@RequestMapping(Constants.DEAL_BASE_PATH)
public class DealController {

    private DealOperations dealOperations;
    public DealController(DealOperations dealOperations) {
        this.dealOperations = dealOperations;
    }

    @GetMapping
    public String getDeal(
            @RequestParam("productId") Optional<String> productId,
            @RequestParam("id") Optional<String> id
            ) {
        if (id.isPresent()) {
            return dealOperations.getById(id.get());
        } else if (productId.isPresent()) {
            return dealOperations.getByProduct(productId.get());
        }
        return "Not Found";
     }

    @PostMapping
    public String addDeal(){
        return dealOperations.createDeal().toString();
    }

    @DeleteMapping
    public String deleteDeal() {
        return  "DELETE DEAL";
    }
}
