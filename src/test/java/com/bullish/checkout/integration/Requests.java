package com.bullish.checkout.integration;

import com.bullish.checkout.integration.stubs.ProductStub;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

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
                    .post("/basket/1/product" .formatted(this.basketId))
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
                    .patch("/basket/%s/product" .formatted(this.basketId))
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
                    .delete("/basket/%s/product" .formatted(this.basketId))
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
                    .post("/basket/%s/checkout" .formatted(this.basketId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

    public static class AddFlatDiscountDealRequest {
        public int productId;
        public int minimumQuantity = 1;
        public int maximumQuantity = 10;

        public int discount;
        private String username;
        private String password;

        public AddFlatDiscountDealRequest(
                int productId,
                int discount,
                String username,
                String password
        ) {
            this.productId = productId;
            this.discount = discount;
            this.username = username;
            this.password = password;
        }
        public MockHttpServletRequestBuilder build() {
            return MockMvcRequestBuilders
                    .post("/deal")
                    .content("""
                            {
                                "productId": %s,
                                "dealType": "FLAT_AMOUNT",
                                "flatDiscount": %s,
                                "minimumQuantity": %s,
                                "maximumQuantity": %s
                                
                            }
                            """.formatted(
                            ProductStub.getById(this.productId).id,
                            this.discount,
                            this.minimumQuantity,
                            this.maximumQuantity
                    ))
                    .with(httpBasic(username, password))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

    public static class AddPercentageDiscountDealRequest {
        public int productId;
        public int minimumQuantity = 1;
        public int maximumQuantity = 10;
        public int percentage;

        private String username;
        private String password;

        public AddPercentageDiscountDealRequest(
                int productId,
                int percentage,
                String username,
                String password
        ) {
            this.productId = productId;
            this.percentage = percentage;
            this.username = username;
            this.password = password;
        }

        public MockHttpServletRequestBuilder build() {
            return MockMvcRequestBuilders
                    .post("/deal")
                    .content("""
                            {
                                "productId": %s,
                                "dealType": "PERCENTAGE",
                                "discountPercentage": %s,
                                "minimumQuantity": %s,
                                "maximumQuantity": %s
                                
                            }
                            """.formatted(
                            ProductStub.getById(this.productId).id,
                            this.percentage,
                            this.minimumQuantity,
                            this.maximumQuantity
                    ))
                    .with(httpBasic(username, password))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

    public static class DeleteDealRequest {
        public int dealId;
        String username;
        String password;

        public DeleteDealRequest(
                int productId,
                String username,
                String password
        ) {
            this.dealId = productId;
            this.username = username;
            this.password = password;
        }

        public MockHttpServletRequestBuilder build() {
            return MockMvcRequestBuilders
                    .delete("/deal/%s" .formatted(this.dealId))
                    .with(httpBasic(username, password))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

    public static class AddProductRequest {
        public double price;
        public String name;
        private String username;
        private String password;

        public AddProductRequest(
                double price,
                String name,
                String username,
                String password
        ) {
            this.price = price;
            this.name = name;
            this.username = username;
            this.password = password;
        }

        public MockHttpServletRequestBuilder build() {
            return MockMvcRequestBuilders
                    .post("/product")
                    .content("""
                            {
                                "price": %s,
                                "name": "%s"                            
                            }
                            """.formatted(
                            this.price,
                            this.name
                    ))
                    .with(httpBasic(username, password))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

    public static class DeleteProductRequest {
        public int id;
        String username;
        String password;

        public DeleteProductRequest(
                int productId,
                String username,
                String password
        ) {
            this.id = productId;
            this.username = username;
            this.password = password;
        }

        public MockHttpServletRequestBuilder build() {
            return MockMvcRequestBuilders
                    .delete("/product/%s" .formatted(this.id))
                    .with(httpBasic(username, password))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }

}
