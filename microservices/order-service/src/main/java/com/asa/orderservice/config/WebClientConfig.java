package com.asa.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(){ //bean is created with the name of method, here bean name is webClient
        return WebClient.builder().build();
    }
}
