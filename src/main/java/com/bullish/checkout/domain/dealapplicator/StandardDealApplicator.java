package com.bullish.checkout.domain.dealapplicator;

import com.bullish.checkout.Constants;
import com.bullish.checkout.domain.BasketCheckout;
import com.bullish.checkout.domain.BasketLineItemWithDeal;
import com.bullish.checkout.domain.Deal;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;

/*
    As mentioned in DealApplicator.java, this is a basic implementation that goes through
    each line item in basket, applies the relevant deals on them and picks the deal that
    maximises discount value for each of them.
 */

public class StandardDealApplicator implements DealApplicator {

    private BasketCheckout basketCheckout;

    public StandardDealApplicator(BasketCheckout basketCheckout) {
        this.basketCheckout = basketCheckout;
    }

    @Override
    public BasketCheckout calculate() {
        /*
           Two things:
           1) This is an assumption we can make since we've already established that all products have the same currency
           2) We can change startingDiscount to something else in case, we want to offer a blanket discount for a basket
         */
        Money startingDiscount = Money.of(0, Constants.DEFAULT_CURRENCY);

        // Calculate total discount
        Money totalDiscount = basketCheckout.getLineItemWithDeals().stream()
                .reduce(startingDiscount, this::calculateLineItemDiscount, Money::add);

        // Calculate net amount for basket
        Money netAmount =  basketCheckout.getTotalAmount().subtract(totalDiscount);

        basketCheckout.setNetAmount(netAmount);

        return basketCheckout;

    }

    private Money calculateLineItemDiscount(Money amount, BasketLineItemWithDeal basketLineItemWithDeal) {
        // We can change startingDiscount to something else in case, we want to offer a blanket discount for a line item
        Money startingDiscount = Money.of(0, Constants.DEFAULT_CURRENCY);

        /*
            We can have multiple ways to apply deals for each line item, such as:
            - picking the deal with the highest discount value
            - combining all the deals together
            - applying the latest deal that was created in the DB
            But for simplicity and since no explicit requirement was given, let's just go with #1
         */

        return basketLineItemWithDeal.getDeals()
                .stream()
                .map(deal -> this.applyDealOnLineItem(deal, basketLineItemWithDeal))
                .max(this::pickHighestDiscount)
                .orElse(startingDiscount);
    }

    private int pickHighestDiscount(Money discount1, Money discount2) {
        if (discount1.isGreaterThan(discount2)) {
            return 1;
        } else if (discount2.isGreaterThan(discount1)) {
            return -1;
        }
        return 0;
    }

    private Money applyDealOnLineItem(Deal deal, BasketLineItemWithDeal basketLineItemWithDeal) {
        CurrencyUnit currency = Constants.DEFAULT_CURRENCY;
        int itemQuantity = basketLineItemWithDeal.getBasketLineItem().getQuantity();
        Money discount = Money.of(0, currency);

        if (itemQuantity < deal.getMinimumQuantity() ||
                itemQuantity > deal.getMaximumQuantity()) {
            return Money.of(0, currency);
        }

        switch (deal.getType()) {
            case PERCENTAGE -> discount = basketLineItemWithDeal
                    .getOriginalPrice()
                    .multiply(deal.getDiscountPercentage())
                    .divide(100);

            case FLAT_AMOUNT -> discount = deal.getFlatDiscount();
        }

        // What if the discount is higher than the price of the items itself?
        if (discount.isGreaterThan(basketLineItemWithDeal.getOriginalPrice())) {
            return basketLineItemWithDeal.getOriginalPrice();
        }

        basketLineItemWithDeal.setDiscountedPrice(
                basketLineItemWithDeal.getOriginalPrice()
                        .subtract(discount)
        );
        return discount;

    }

}
