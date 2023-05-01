package com.bullish.checkout.configuration;

import com.bullish.checkout.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class AuthenticationAndSecurityConfiguration {

    @Value( "${api.username}" )
    private String username;

    @Value( "${api.password}" )
    private String password;

    private final String ADMIN_ROLE = "ADMIN";
    private final String[] protectedPaths = Stream.of(Constants.PRODUCT_BASE_PATH, Constants.DEAL_BASE_PATH)
            .map(x -> x+"/**").toArray(String[]::new);

    private final String[] unprotectedPaths = Stream.of(Constants.BASKET_BASE_PATH, Constants.ERROR_PATH)
            .map(x -> x+"/**").toArray(String[]::new);


    @Bean
    @Order(2)
    public SecurityFilterChain allSecurity(HttpSecurity http) throws Exception {
        // NOT OK FOR PROD. But for the purpose of the assignment, it'll do
        http.cors().and().csrf().disable();

        http.authorizeHttpRequests(authorizer -> {
            try {
                // Allow all calls to /basket to go through without security
                authorizer.requestMatchers(unprotectedPaths).permitAll().and().anonymous();

                // All calls to other endpoints require admin credentials
                authorizer.requestMatchers(protectedPaths).authenticated()
                        .and()
                        .httpBasic(Customizer.withDefaults());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return http.build();
    }


    @Bean
    public UserDetailsService adminUserDetailsService() {
        // Dont use defaultPasswordEncoder in PROD.
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username(username)
                .password(password)
                .roles(ADMIN_ROLE)
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
