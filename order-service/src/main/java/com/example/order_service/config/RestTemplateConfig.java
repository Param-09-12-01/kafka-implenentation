package com.example.order_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {


    @Value("${inventory.service.url:http://localhost:8080}")
    private String inventoryServiceURL;

    @Bean("inventoryRestTemplate")
    public RestTemplate inventoryRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        DefaultUriBuilderFactory factory =
                new DefaultUriBuilderFactory(inventoryServiceURL);

        restTemplate.setUriTemplateHandler(factory);

        return restTemplate;
    }
}
