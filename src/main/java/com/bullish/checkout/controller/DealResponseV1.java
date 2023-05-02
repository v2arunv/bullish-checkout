package com.bullish.checkout.controller;

import com.bullish.checkout.domain.Deal;

import java.math.BigDecimal;

public class DealResponseV1 {

    private Long id;

    private String dealType;

    private int minimumQuantity;
    private int maximumQuantity;

    private ProductDtoV1 product;

    private MoneyDtoV1 flatDiscount;

    private int discountPercentage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public int getMaximumQuantity() {
        return maximumQuantity;
    }

    public void setMaximumQuantity(int maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public ProductDtoV1 getProduct() {
        return product;
    }

    public void setProduct(ProductDtoV1 product) {
        this.product = product;
    }

    public MoneyDtoV1 getFlatDiscount() {
        return flatDiscount;
    }

    public void setFlatDiscount(MoneyDtoV1 flatDiscount) {
        this.flatDiscount = flatDiscount;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public static DealResponseV1 from(Deal deal) {
        DealResponseV1 response = new DealResponseV1();
        response.setId(deal.getId());
        response.setDealType(deal.getType().name());
        response.setMinimumQuantity(deal.getMinimumQuantity().intValue());
        response.setMaximumQuantity(deal.getMaximumQuantity().intValue());
        response.setProduct(ProductDtoV1.from(deal.getProduct()));
        response.setFlatDiscount(MoneyDtoV1.from(deal.getFlatDiscount()));
        response.setDiscountPercentage(safelyAccessBigDecimal(deal.getDiscountPercentage()));
        return response;
    }

    private static int safelyAccessBigDecimal(BigDecimal value) {
        if (value == null) return 0;
        return value.intValue();
    }
}
