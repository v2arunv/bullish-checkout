package com.bullish.checkout.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminIntegrationTest {

    @Value("${api.username}")
    public String username;

    @Value("${api.password}")
    public String password;

    @Autowired
    private MockMvc mockMvc;



    @Test
    public void test401WhenAccessingWithoutBasicAuthenticationCredentials() throws Exception {
        var request = MockMvcRequestBuilders.post("/product");

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void test200WithAccessingWithBasicAuthenticationCredentials() throws Exception {
        var request = MockMvcRequestBuilders.post("/product")
                .with(httpBasic(username, password));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
