package com.bullish.checkout.controller;

import org.javamoney.moneta.Money;

class MoneyDtoV1 {

    private String currency;
    private String amount;

    private MoneyDtoV1(Money money) {
        this.currency = money.getCurrency().getCurrencyCode();
        this.amount = money.getNumberStripped().toPlainString();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public static MoneyDtoV1 from(Money money) {
        return new MoneyDtoV1(money);
    }
}
