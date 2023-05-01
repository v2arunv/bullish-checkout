package com.bullish.checkout.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/test-schema.sql","/custom-data.sql"})
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testCreateBasket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket"))
                .andExpect(status().isOk())
                .andDo(x -> {
                    System.out.println(x.getResponse().getContentAsString());
                })
                .andExpect(jsonPath("$.id").value("1"));

    }
    @Test
    public void testAddProductToBasket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/basket/product"))
                .andExpect(status().isOk())
                .andExpect(content().string("1-SAMPLE, "));
    }
}
