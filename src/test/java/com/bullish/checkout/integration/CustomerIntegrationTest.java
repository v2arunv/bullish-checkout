package com.bullish.checkout.integration;

import com.bullish.checkout.integration.stubs.ProductStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-schema.sql","/custom-data.sql"})
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testCreateBasket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.items").isEmpty());

    }

    @Test
    public void testGetBasket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket"));

        mockMvc.perform(MockMvcRequestBuilders.get("/basket/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.items").isEmpty());
    }



    @Nested
    public class PostBucketCreationTests {
        @BeforeEach
        public void createBasket() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/basket"));;
        }

        @Test
        public void testAddOneProduct() throws Exception {
            var request = new AddProductToBasketRequest(1, 1);

            mockMvc.perform(request.build())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.id").value(ProductStubs.getId(request.productId)))
                    .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStubs.getPrice(request.productId)))
                    .andExpect(jsonPath("$.items[0].quantity").value(request.quantity));
        }

        @Test
        public void testAddTwoProducts() throws Exception {
            int productOneStubIndex = 0;
            int productTwoStubIndex = 1;

            int productOneId = ProductStubs.getId(0).intValue();
            int productTwoId = ProductStubs.getId(1).intValue();

            var request1 = new AddProductToBasketRequest(productOneId, 1);
            var request2 = new AddProductToBasketRequest(productTwoId, 3);


            mockMvc.perform(request1.build());

            mockMvc.perform(request2.build())
                    // all paths exist
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(2)))
                    .andExpect(jsonPath("$.items[*].product.price").hasJsonPath())
                    .andExpect(jsonPath("$.items[*].product.price.amount").hasJsonPath())
                    .andExpect(jsonPath("$.items[*].product.price.currency").hasJsonPath())
                    .andExpect(jsonPath("$.items[*].product.name").hasJsonPath())
                    .andExpect(jsonPath("$.items[*].quantity").hasJsonPath())
                    // specifics
                    .andExpect(jsonPath("$.items[*].product.id").value(
                            containsInAnyOrder(
                                    productOneId,
                                    productTwoId
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].product.price.amount").value(
                            containsInAnyOrder(
                                    String.valueOf(ProductStubs.getPrice(0)),
                                    String.valueOf(ProductStubs.getPrice(1))
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].product.name").value(
                            containsInAnyOrder(
                                    ProductStubs.getName(0),
                                    ProductStubs.getName(1)
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].quantity").value(
                            containsInAnyOrder(request1.quantity, request2.quantity)
                    ));

        }

        @Test
        public void testAddSameProductInTwoRequestsAndExpectOnlyOneLineItemInBasket() throws Exception {
            var first = new AddProductToBasketRequest(0, 5);
            var second = new AddProductToBasketRequest(0, 10);

            mockMvc.perform(first.build());

            mockMvc.perform(second.build())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items").isNotEmpty())
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].product.id").value(ProductStubs.getId(first.productId)))
                    .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStubs.getPrice(first.productId)))
                    .andExpect(jsonPath("$.items[0].quantity").value(first.quantity + second.quantity));
        }

        @Test
        public void testAddProductThatDoesNotExistToBasket() throws Exception {
            var request = new AddProductToBasketRequest(5);

            mockMvc.perform(request.build())
                    .andExpect(status().isNotFound())
                    .andExpect(status().reason(containsString("not found")));
        }

        @Test
        public void testAddProductWithZeroQuantityProductToBasket() throws Exception {
            var request = new AddProductToBasketRequest(1,0);

            mockMvc.perform(request.build())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(containsString("Product quantity cannot be zero")));
        }

        @Test
        public void testAddProductsAndReduceQuantity() throws Exception {
            var addRequest = new AddProductToBasketRequest(2, 5);
            var patchRequest = new PatchProductInBasketRequest(2, 3);

            mockMvc.perform(addRequest.build());

            mockMvc.perform(patchRequest.build())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.id").value(ProductStubs.getId(patchRequest.productId)))
                    .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStubs.getPrice(patchRequest.productId)))
                    .andExpect(jsonPath("$.items[0].quantity").value(patchRequest.quantity));
        }

    }

    private class AddProductToBasketRequest {
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
            Long productId = this.isInvalid ? 1000L : ProductStubs.getId(this.productId);

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

    private class PatchProductInBasketRequest {
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
                        """.formatted(ProductStubs.getId(this.productId), quantity))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
    }


}
