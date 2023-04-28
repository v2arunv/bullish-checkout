package com.bullish.checkout.controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
    Administrative endpoints to add and remove deals
 */
@RestController
@RequestMapping("/deal")
public class DealController {

    @PostMapping("/")
    public String addDeal(){
        return "ADD DEAL";
    }

    @DeleteMapping("/")
    public String deleteDeal() {
        return  "DELETE DEAL";
    }
}
