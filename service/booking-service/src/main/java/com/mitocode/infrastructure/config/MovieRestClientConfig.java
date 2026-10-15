package com.mitocode.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MovieRestClientConfig {
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient movieServiceRestClient(@LoadBalanced RestClient.Builder builder,
            @Value("${movie-service.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
