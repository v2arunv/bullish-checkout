package com.bullish.checkout.integration;

import com.bullish.checkout.integration.stubs.ProductStub;
import com.bullish.checkout.integration.Requests.AddProductToBasketRequest;
import com.bullish.checkout.integration.Requests.PatchProductInBasketRequest;
import com.bullish.checkout.integration.Requests.DeleteProductInBasketRequest;
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


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-schema.sql","/custom-data.sql"})
public class BasketCRUDIntegrationTest {

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
    public class AddToBucketTests {
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
                    .andExpect(jsonPath("$.items[0].product.id").value(ProductStub.getById(request.productId).id))
                    .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStub.getById(request.productId).price))
                    .andExpect(jsonPath("$.items[0].quantity").value(request.quantity));
        }

        @Test
        public void testAddTwoProducts() throws Exception {

            var request1 = new AddProductToBasketRequest(1, 1);
            var request2 = new AddProductToBasketRequest(2, 3);


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
                                    request1.productId,
                                    request2.productId
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].product.price.amount").value(
                            containsInAnyOrder(
                                    String.valueOf(ProductStub.getById(request1.productId).price),
                                    String.valueOf(ProductStub.getById(request2.productId).price)
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].product.name").value(
                            containsInAnyOrder(
                                    ProductStub.getById(request1.productId).name,
                                    ProductStub.getById(request2.productId).name
                            )
                    ))
                    .andExpect(jsonPath("$.items[*].quantity").value(
                            containsInAnyOrder(request1.quantity, request2.quantity)
                    ));

        }

        @Test
        public void testAddSameProductInTwoRequestsAndExpectOnlyOneLineItemInBasket() throws Exception {
            var first = new AddProductToBasketRequest(1, 5);
            var second = new AddProductToBasketRequest(1, 10);

            mockMvc.perform(first.build());

            mockMvc.perform(second.build())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items").isNotEmpty())
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].product.id").value(ProductStub.getById(first.productId).id))
                    .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStub.getById(first.productId).id))
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



    }

    @Nested
    public class PatchProductsInBasketTests {
        @BeforeEach
        public void createBasket() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/basket"));
        }
        @Test
        public void testAddProductsAndReduceQuantity() throws Exception {
            var addRequest = new AddProductToBasketRequest(2, 5);
            var patchRequest = new PatchProductInBasketRequest(2, 3);

            mockMvc.perform(addRequest.build());

            mockMvc.perform(patchRequest.build())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.id").value(ProductStub.getById(patchRequest.productId).id))
                    .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStub.getById(patchRequest.productId).price))
                    .andExpect(jsonPath("$.items[0].quantity").value(patchRequest.quantity));
        }

        @Test
        public void testPatchProductInBasketWithNoProducts() throws Exception {
            var patchRequest = new PatchProductInBasketRequest(2, 3);

            mockMvc.perform(patchRequest.build())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(containsString("does not exist in this basket")));
        }

        @Test
        public void testPatchProductToAQuantityOfZero() throws Exception {
            var addRequest = new AddProductToBasketRequest(2, 5);
            var patchRequest = new PatchProductInBasketRequest(2, 0);

            mockMvc.perform(addRequest.build());

            mockMvc.perform(patchRequest.build());

            mockMvc.perform(MockMvcRequestBuilders.get("/basket/1"))
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items").isEmpty());

        }
    }

    @Nested
    public class DeleteProductsInBasketTests {
        @BeforeEach
        public void createBasket() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/basket"));
        }

        @Test
        public void testAddProductsAndDelete() throws Exception {
            var addRequest = new AddProductToBasketRequest(2, 5);
            var deleteRequest = new DeleteProductInBasketRequest(addRequest.productId);

            mockMvc.perform(addRequest.build());
            mockMvc.perform(deleteRequest.build());

            mockMvc.perform(MockMvcRequestBuilders.get("/basket/1"))
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items").isEmpty());
        }

        @Test
        public void testAddMultipleProductsAndDeleteOne() throws Exception {
            var addRequestOne = new AddProductToBasketRequest(1, 10);
            var addRequestTwo = new AddProductToBasketRequest(2, 5);
            var deleteRequest = new DeleteProductInBasketRequest(addRequestOne.productId);

            mockMvc.perform(addRequestOne.build());
            mockMvc.perform(addRequestTwo.build());
            mockMvc.perform(deleteRequest.build());

            mockMvc.perform(MockMvcRequestBuilders.get("/basket/1"))
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].product.id").value(ProductStub.getById(addRequestTwo.productId).id))
                    .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStub.getById(addRequestTwo.productId).price))
                    .andExpect(jsonPath("$.items[0].quantity").value(addRequestTwo.quantity));
        }

        @Test
        public void testDeleteOnProductThatDoesNotExistInBucket() throws Exception {
            var deleteRequest = new DeleteProductInBasketRequest(1);

            mockMvc.perform(deleteRequest.build())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(containsString("does not exist in this basket")));

        }


    }






}
