package com.bullish.checkout;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class Constants {

    // Storing all reused string constants
    public static final String DEAL_BASE_PATH = "/deal";
    public static final String PRODUCT_BASE_PATH = "/product";
    public static final String BASKET_BASE_PATH = "/basket";

    public static final String ERROR_PATH = "/error";
    public static final String H2_UI = "/h2";

    // Currencies
    public static final CurrencyUnit DEFAULT_CURRENCY = Monetary.getCurrency("HKD");

}
