package com.bullish.checkout.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
public class CustomerSecurityConfiguration {

    @Bean
    @Order(2)
    public SecurityFilterChain customerSecurity(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.authorizeHttpRequests(customizer -> {
            try {
                customizer.anyRequest().permitAll().and().anonymous();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return http.build();
    }
}
