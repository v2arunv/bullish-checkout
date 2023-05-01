package com.bullish.checkout.integration.stubs;

import com.bullish.checkout.domain.Product;

public class ProductStubs {
    private static Long[] ids = new Long[]{1L, 2L, 3L};
    private static String[] names = new String[]{
            "Macbook Pro",
            "iPhone",
            "AirPods Pro"
    };

    private static int[] prices = new int[] {
            1299,
            999,
            499
    };

    public static String getName(int index) {
        return names[index];
    }

    public static Long getId(int index) {
        return ids[index];
    }

    public static int getPrice(int index) {
        return prices[index];
    }
}
