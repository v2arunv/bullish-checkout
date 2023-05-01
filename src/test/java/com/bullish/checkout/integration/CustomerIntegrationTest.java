package com.bullish.checkout.integration;

import com.bullish.checkout.integration.stubs.ProductStubs;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-schema.sql","/custom-data.sql"})
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    private MockHttpServletRequestBuilder createAddProductRequests(int index, int quantity) {
        return MockMvcRequestBuilders
                .post("/basket/1/product")
                .content("""
                        {
                            "productId": %s,
                            "quantity": %s
                        }
                        """.formatted(ProductStubs.getId(index), quantity))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testCreateBasket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.items").isEmpty());

        //                .andDo(x -> {
//                    System.out.println(x.getResponse().getContentAsString());
//                })

    }

    @Test
    public void testGetBasket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket"));

        mockMvc.perform(MockMvcRequestBuilders.get("/basket/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    public void testAddOneProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket"));
        int productStubIndex = 0;

        mockMvc.perform(createAddProductRequests(productStubIndex, 1))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].product.id").value(ProductStubs.getId(productStubIndex)))
                .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStubs.getPrice(productStubIndex)))
                .andExpect(jsonPath("$.items[0].quantity").value(1));
    }

    @Test
    public void testAddTwoProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket"));

        int productOneStubIndex = 0;
        int productTwoStubIndex = 1;


        var request1 = createAddProductRequests(productOneStubIndex, 1);

        var request2 = createAddProductRequests(productTwoStubIndex, 3);


        mockMvc.perform(request1);

        mockMvc.perform(request2)
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items", hasSize(2)))
                // Product 1
                .andExpect(jsonPath("$.items[0].product.id").value(ProductStubs.getId(productOneStubIndex)))
                .andExpect(jsonPath("$.items[0].product.price").isNotEmpty())
                .andExpect(jsonPath("$.items[0].product.price.amount").value(ProductStubs.getPrice(productOneStubIndex)))
                .andExpect(jsonPath("$.items[0].quantity").value(1))
                // Product 2
                .andExpect(jsonPath("$.items[1].product.id").value(ProductStubs.getId(productTwoStubIndex)))
                .andExpect(jsonPath("$.items[1].product.price").isNotEmpty())
                .andExpect(jsonPath("$.items[1].product.price.amount").value(ProductStubs.getPrice(productTwoStubIndex)))
                .andExpect(jsonPath("$.items[1].quantity").value(3));
    }

    @Nested
    public class PostBucketCreationTests {
        public void createBasket() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/basket"));;
        }



    }


}
