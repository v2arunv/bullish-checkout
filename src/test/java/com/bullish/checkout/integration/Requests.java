package com.bullish.checkout.integration;

import com.bullish.checkout.integration.stubs.ProductStub;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class Requests {
    public static class AddProductToBasketRequest {
        public int productId;
        public int quantity;
        public int basketId;

        private boolean isInvalid;

        public AddProductToBasketRequest(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
            this.basketId = 1;
            this.isInvalid = false;
        }

        public AddProductToBasketRequest(int quantity) {
            this.isInvalid = true;
            this.quantity = quantity;
        }

        public MockHttpServletRequestBuilder build() {
            Long productId = this.isInvalid ? 1000L : ProductStub.getById(this.productId).id;

            return MockMvcRequestBuilders
                    .post("/basket/1/product".formatted(this.basketId))
                    .content("""
                        {
                            "productId": %s,
                            "quantity": %s
                        }
                        """.formatted(productId, quantity))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

    public static class PatchProductInBasketRequest {
        public int productId;
        public int quantity;
        public int basketId;

        public PatchProductInBasketRequest(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
            this.basketId = 1;
        }

        public MockHttpServletRequestBuilder build() {
            return MockMvcRequestBuilders
                    .patch("/basket/%s/product".formatted(this.basketId))
                    .content("""
                        {
                            "productId": %s,
                            "quantity": %s
                        }
                        """.formatted(ProductStub.getById(this.productId).id, quantity))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

    public static class DeleteProductInBasketRequest {
        public int productId;
        public int basketId;

        public DeleteProductInBasketRequest(int productId) {
            this.productId = productId;
            this.basketId = 1;
        }

        public MockHttpServletRequestBuilder build() {
            return MockMvcRequestBuilders
                    .delete("/basket/%s/product".formatted(this.basketId))
                    .content("""
                        {
                            "productId": %s
                        }
                        """.formatted(ProductStub.getById(this.productId).id))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

    public static class CheckoutInBasketRequest {
        public int basketId;

        public CheckoutInBasketRequest() {
            this.basketId = 1;
        }

        public MockHttpServletRequestBuilder build() {
            return MockMvcRequestBuilders
                    .post("/basket/%s/checkout".formatted(this.basketId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }
}
