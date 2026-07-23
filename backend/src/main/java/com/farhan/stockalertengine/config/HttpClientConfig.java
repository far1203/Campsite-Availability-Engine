package com.farhan.stockalertengine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class HttpClientConfig {

    @Value("${ridb.api.key}")
    private String apiKey;


    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://ridb.recreation.gov/api/v1")
                .defaultHeader("apikey", apiKey)
                .build();
    }

}
