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
import java.math.RoundingMode;

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

    @BeforeEach
    public void createBasket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket"));
    }


    @Nested
    public class SimpleCheckoutWithNoDeals {

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



        @Test
        public void testWhenMultipleProductsHaveOneEligibleDealEach() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequestOne = new AddProductToBasketRequest(1, 10);
            var addProductRequestTwo = new AddProductToBasketRequest(2, 10);

            BigDecimal expectedTotalAmount =
                    BigDecimal.valueOf(addProductRequestOne.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestOne.productId).price))
                            .add(BigDecimal.valueOf(addProductRequestTwo.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestTwo.productId).price)
                    ));


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
                    // check total and net amounts per line item and in total
                    .andExpect(jsonPath("$.items[*].netAmount.amount").value(
                            containsInAnyOrder(
                                    BigDecimal.valueOf(8991).toString(),
                                    BigDecimal.valueOf(6495).toString()
                            )
                    ))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(BigDecimal.valueOf(15486).toString()));

        }

        @Sql({"/multiple-percentage-deals-for-single-product.sql"})
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

        @Sql({"/multiple-percentage-deals-for-multiple-products.sql"})
        @Test
        public void testWhenMultipleProductsHaveMultipleEligibleDealsEach() throws Exception{

            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequestOne = new AddProductToBasketRequest(1, 10);
            var addProductRequestTwo = new AddProductToBasketRequest(2, 10);

            BigDecimal expectedTotalAmount =
                    BigDecimal.valueOf(addProductRequestOne.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestOne.productId).price))
                            .add(BigDecimal.valueOf(addProductRequestTwo.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestTwo.productId).price)
                            ));

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
                    // check total and net amounts per line item and in total
                    .andExpect(jsonPath("$.items[*].netAmount.amount").value(
                            containsInAnyOrder(
                                    BigDecimal.valueOf(9742.5).toString(),
                                    BigDecimal.valueOf(6993).toString()
                            )
                    ))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(BigDecimal.valueOf(16735.5).toString()));
        }
    }

    @Nested
    @Sql({"/single-flat-discount-deals-per-product.sql"})
    public class CheckoutWithFlatDiscountDeals {
        @Test
        public void testWhenOneProductHasOneEligibleDeal() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(1, 5);

            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            BigDecimal expectedNetAmount = expectedTotalAmount.subtract(BigDecimal.valueOf(500));

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedNetAmount.toString()));
        }

        @Test
        public void testWhenMultipleProductsHaveOneEligibleDealEach() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequestOne = new AddProductToBasketRequest(1, 10);
            var addProductRequestTwo = new AddProductToBasketRequest(2, 10);

            BigDecimal expectedTotalAmount =
                    BigDecimal.valueOf(addProductRequestOne.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestOne.productId).price))
                            .add(BigDecimal.valueOf(addProductRequestTwo.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestTwo.productId).price)
                            ));


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
                    // check total and net amounts per line item and in total
                    .andExpect(jsonPath("$.items[*].netAmount.amount").value(
                            containsInAnyOrder(
                                    BigDecimal.valueOf(9890).toString(),
                                    BigDecimal.valueOf(12490).toString()
                            )
                    ))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(BigDecimal.valueOf(22380).toString()));

        }


        @Test
        public void testWithOneProductWhoseFlatDiscountIsGreaterThanLineItemPrice() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(3, 5);
            var addProductRequestTwo = new AddProductToBasketRequest(2, 10);


            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            BigDecimal expectedNetAmount = BigDecimal.ZERO;

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedNetAmount.toString()));
        }

        @Test
        public void testWhenMultipleProductsButOnProductWhoseFlatDiscountIsGreaterThanLineItemPrice() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequestOne = new AddProductToBasketRequest(3, 10);
            var addProductRequestTwo = new AddProductToBasketRequest(2, 10);

            BigDecimal expectedTotalAmount =
                    BigDecimal.valueOf(addProductRequestOne.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestOne.productId).price))
                            .add(BigDecimal.valueOf(addProductRequestTwo.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestTwo.productId).price)
                            ));

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
                    // check total and net amounts per line item and in total
                    .andExpect(jsonPath("$.items[*].netAmount.amount").value(
                            containsInAnyOrder(
                                    BigDecimal.ZERO.toString(),
                                    BigDecimal.valueOf(9890).toString()
                            )
                    ))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(BigDecimal.valueOf(9890).toString()));

        }


        @Test
        @Sql({"/multiple-flat-discount-deals-for-single-product.sql"})
        public void testWhenOneProductHasMultipleEligibleDeals() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(2, 5);

            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            BigDecimal expectedNetAmount = expectedTotalAmount.subtract(BigDecimal.valueOf(200));

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedNetAmount.toString()));
        }



        @Test
        @Sql({"/multiple-flat-discount-deals-for-multiple-products.sql"})
        public void testWhenMultipleProductsHaveMultipleEligibleDealsEach() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequestOne = new AddProductToBasketRequest(2, 10);
            var addProductRequestTwo = new AddProductToBasketRequest(3, 10);

            BigDecimal expectedTotalAmount =
                    BigDecimal.valueOf(addProductRequestOne.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestOne.productId).price))
                            .add(BigDecimal.valueOf(addProductRequestTwo.quantity).multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequestTwo.productId).price)
                            ));

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
                    // check total and net amounts per line item and in total
                    .andExpect(jsonPath("$.items[*].netAmount.amount").value(
                            containsInAnyOrder(
                                    BigDecimal.valueOf(9790).toString(),
                                    BigDecimal.valueOf(4740).toString()
                            )
                    ))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(BigDecimal.valueOf(14530).toString()));

        }
    }

    @Nested
    @Sql({"/multiple-deal-types.sql"})
    public class CheckoutWithMultipleProductsWhichHaveMultipleTypesOfDeals {

        @Test
        public void testWhenOneProductHasMultipleTypesOfDealsAndFlatDiscountIsABetterOption() throws Exception{
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(1, 4);

            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            BigDecimal expectedNetAmount = expectedTotalAmount.subtract(BigDecimal.valueOf(5000));

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedNetAmount.toString()));

        }

        @Test
        public void testWhenOneProductHasMultipleTypesOfDealsAndPercentageDiscountIsABetterOption() throws Exception {
            var checkoutRequest = new CheckoutInBasketRequest();
            var addProductRequest = new AddProductToBasketRequest(2, 4);

            BigDecimal expectedTotalAmount = BigDecimal.valueOf(addProductRequest.quantity)
                    .multiply(BigDecimal.valueOf(ProductStub.getById(addProductRequest.productId).price));

            BigDecimal expectedNetAmount = expectedTotalAmount.multiply(BigDecimal.valueOf(0.5)).setScale(0, RoundingMode.UP);

            mockMvc.perform(addProductRequest.build());

            mockMvc.perform(checkoutRequest.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount.amount").value(expectedTotalAmount.toString()))
                    .andExpect(jsonPath("$.netAmount.amount").value(expectedNetAmount.toString()));

        }

    }
}
