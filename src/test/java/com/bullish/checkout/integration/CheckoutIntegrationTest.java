package com.bullish.checkout.integration;

import com.bullish.checkout.integration.stubs.ProductStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.bullish.checkout.integration.Requests.CheckoutInBasketRequest;
import com.bullish.checkout.integration.Requests.AddProductToBasketRequest;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-schema.sql", "/products.sql"})
public class CheckoutIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Nested
    public class SimpleCheckoutWithNoDeals {
        @BeforeEach
        public void createBasket() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/basket"));
        }

        @Test
        public void testWhenNoProductsAreAdded() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items").isEmpty())
                    .andExpect(jsonPath("$.totalAmount.amount").value("0"))
                    .andExpect(jsonPath("$.netAmount.amount").value("0"));
        }

        @Test
        public void testWhenOneProductIsAdded() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(1, 5);

            int expectedAmount = addProductRequest.quantity * ProductStub.getById(addProductRequest.productId).price;

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(String.valueOf(expectedAmount)))
                    .andExpect(jsonPath("$.netAmount.amount").value(String.valueOf(expectedAmount)));

        }

        @Test
        public void testWhenMultipleProductsAreAdded() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequestOne = new AddProductToBasketRequest(1, 1);
            var addProductRequestTwo = new AddProductToBasketRequest(2, 1);

            int expectedAmount = (addProductRequestOne.quantity * ProductStub.getById(addProductRequestOne.productId).price) +
                    (addProductRequestTwo.quantity * ProductStub.getById(addProductRequestTwo.productId).price);

            mockMvc.perform(addProductRequestOne.build());
            mockMvc.perform(addProductRequestTwo.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(2)))
                    .andExpect(jsonPath("$.items[*].product.price").hasJsonPath())
                    .andExpect(jsonPath("$.items[*].product.price.amount").hasJsonPath())
                    .andExpect(jsonPath("$.items[*].product.price.currency").hasJsonPath())
                    .andExpect(jsonPath("$.items[*].product.name").hasJsonPath())
                    .andExpect(jsonPath("$.items[*].quantity").hasJsonPath())
                    // check line items
                    .andExpect(jsonPath("$.items[*].product.id").value(
                            containsInAnyOrder(
                                    addProductRequestOne.productId,
                                    addProductRequestTwo.productId
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].product.price.amount").value(
                            containsInAnyOrder(
                                    String.valueOf(ProductStub.getById(addProductRequestOne.productId).price),
                                    String.valueOf(ProductStub.getById(addProductRequestTwo.productId).price)
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].product.name").value(
                            containsInAnyOrder(
                                    ProductStub.getById(addProductRequestOne.productId).name,
                                    ProductStub.getById(addProductRequestTwo.productId).name
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].quantity").value(
                            containsInAnyOrder(addProductRequestOne.quantity, addProductRequestTwo.quantity)
                    ))
                    // check total and net amounts
                    .andExpect(jsonPath("$.totalAmount.amount").value(String.valueOf(expectedAmount)))
                    .andExpect(jsonPath("$.netAmount.amount").value(String.valueOf(expectedAmount)));

        }
    }

    @Nested
    @Sql({"/single-percentage-deals-per-product.sql"})
    public class CheckoutWithPercentageDiscountDeals {
        @BeforeEach
        public void createBasket() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/basket"));
        }

        @Test
        public void testWhenOneProductHasOneEligibleDeal() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(1, 5);

            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            BigDecimal expectedNetAmount = expectedTotalAmount.divide(BigDecimal.valueOf(2));

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedNetAmount.toString()));

        }

        @Test
        public void testWhenOneProductHasOneEligibleDealButBasketQuantityIsBelowDealMinimumQuantity() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(1, 2);

            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedTotalAmount.toString()));
        }

        @Test
        public void testWhenOneProductHasOneEligibleDealButBasketQuantityIsAboveDealMaximumQuantity() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(1, 50);

            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedTotalAmount.toString()));
        }

        @Sql({"/multiple-percentage-deals-per-product.sql"})
        @Test
        public void testWhenOneProductHasMultipleEligibleDeals() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(1, 5);

            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            BigDecimal expectedNetAmount = expectedTotalAmount.multiply(BigDecimal.valueOf(0.75));

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedNetAmount.toString()));

        }


        @Test
        public void testWhenMultipleProductsHaveOneEligibleDealEach() {

        }

        @Test
        public void testWhenMultipleProductsHaveMultipleEligibleDealsEach() {

        }
    }

    @Nested
    public class CheckoutWithFlatDiscountDeals {
        @Test
        public void testWhenOneProductHasOneEligibleDeal() {

        }

        @Test
        public void testWhenOneProductHasMultipleEligibleDeals() {

        }

        @Test
        public void testWhenMultipleProductsHaveOneEligibleDealEach() {

        }

        @Test
        public void testWhenMultipleProductsHaveMultipleEligibleDealsEach() {

        }
    }

    @Nested
    public class CheckoutWithMultipleProductsWhichHaveMultipleTypesOfDeals {

    }
}
