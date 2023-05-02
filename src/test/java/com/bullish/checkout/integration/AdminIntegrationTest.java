package com.bullish.checkout.integration;

import com.bullish.checkout.integration.stubs.ProductStub;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.bullish.checkout.integration.Requests.AddPercentageDiscountDealRequest;
import com.bullish.checkout.integration.Requests.AddFlatDiscountDealRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminIntegrationTest {

    @Value("${api.username}")
    public String username;

    @Value("${api.password}")
    public String password;

    @Autowired
    private MockMvc mockMvc;


    @Nested
    @Sql({"/test-schema.sql", "/products.sql", "/reset-deals.sql"})
    public class DealOperationsIntegrationTest {

        @Test
        public void testAddANewDealWithoutAuth() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/deal"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void testAddANewPercentageDeal() throws Exception {
            AddPercentageDiscountDealRequest request = new AddPercentageDiscountDealRequest(1, 25, username, password);
            mockMvc.perform(request.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.minimumQuantity").value(request.minimumQuantity))
                    .andExpect(jsonPath("$.maximumQuantity").value(request.maximumQuantity))
                    .andExpect(jsonPath("$.dealType").value("PERCENTAGE"))
                    .andExpect(jsonPath("$.discountPercentage").value(request.percentage));
        }

        @Test
        public void testAddANewFlatDiscountDeal() throws Exception {
            Requests.AddFlatDiscountDealRequest request = new Requests.AddFlatDiscountDealRequest(1, 250, username, password);
            mockMvc.perform(request.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.minimumQuantity").value(request.minimumQuantity))
                    .andExpect(jsonPath("$.maximumQuantity").value(request.maximumQuantity))
                    .andExpect(jsonPath("$.dealType").value("FLAT_AMOUNT"))
                    .andExpect(jsonPath("$.flatDiscount.amount").value(request.discount));
        }

        @Test
        public void testMultipleDealsOnSameProduct() throws Exception {
            AddPercentageDiscountDealRequest requestOne = new AddPercentageDiscountDealRequest(1, 25, username, password);
            AddFlatDiscountDealRequest requestTwo = new Requests.AddFlatDiscountDealRequest(1, 250, username, password);

            mockMvc.perform(requestOne.build());
            mockMvc.perform(requestTwo.build());
            mockMvc.perform(MockMvcRequestBuilders
                    .get("/deal?productId=%s".formatted(requestTwo.productId)).with(httpBasic(username, password)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$.[*].dealType").value(
                            containsInAnyOrder(
                                    "FLAT_AMOUNT",
                                    "PERCENTAGE"
                            )
                    ));

        }

        @Test
        public void testDeleteADeal() throws Exception {
            AddPercentageDiscountDealRequest requestOne = new AddPercentageDiscountDealRequest(1, 25, username, password);
            AddFlatDiscountDealRequest requestTwo = new Requests.AddFlatDiscountDealRequest(1, 250, username, password);
            Requests.DeleteDealRequest deleteRequest = new Requests.DeleteDealRequest(1, username, password);

            mockMvc.perform(requestOne.build());
            mockMvc.perform(requestTwo.build());
            mockMvc.perform(deleteRequest.build());

            mockMvc.perform(MockMvcRequestBuilders
                    .get("/deal/%s".formatted(1)).with(httpBasic(username, password)))
                    .andExpect(status().isNotFound());

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/deal?productId=%s".formatted(requestTwo.productId)).with(httpBasic(username, password)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[*].id").value(not(contains("1"))));

        }
        @Test
        public void testAddANewDealWithMissingParams() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .post("/deal")
                    .content("""
                            {
                                "productId": %s,
                                "dealType": "PERCENTAGE",
                                "minimumQuantity": %s,
                                "maximumQuantity": %s,
                                "flatDiscount": %s
                                
                            }
                            """.formatted(
                            ProductStub.getById(1).id,
                            10,
                            1,
                            100
                    ))
                    .with(httpBasic(username, password))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        public void testAddANewDealWithMissingType() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/deal")
                            .content("""
                            {
                                "productId": %s,
                                "minimumQuantity": %s,
                                "maximumQuantity": %s,
                                "flatDiscount": %s
                                
                            }
                            """.formatted(
                                    ProductStub.getById(1).id,
                                    10,
                                    1,
                                    100
                            ))
                            .with(httpBasic(username, password))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @Sql("/reset-products.sql")
    public class ProductAdminOperationsTest {

        @Test
        public void test401WhenAccessingWithoutBasicAuthenticationCredentials() throws Exception {
            var request = MockMvcRequestBuilders.post("/product");

            mockMvc.perform(request)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void testAddANewProduct() throws Exception {
            String name = "PS5";
            Requests.AddProductRequest request = new Requests.AddProductRequest(100, name, username, password);
            mockMvc.perform(request.build())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.price.amount").value(request.price))
                    .andExpect(jsonPath("$.name").value(request.name));

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/product/%s".formatted(1)).with(httpBasic(username, password)))
                    .andExpect(status().isOk());
        }

        @Test
        public void testDeleteAProduct() throws Exception {
            Requests.DeleteProductRequest deleteProductRequest = new Requests.DeleteProductRequest(1, username, password);
            mockMvc.perform(deleteProductRequest.build())
                    .andExpect(status().isOk());

            mockMvc.perform(MockMvcRequestBuilders
                    .get("/product/%s".formatted(1)).with(httpBasic(username, password)))
                    .andExpect(status().isNotFound());
        }
    }



}
