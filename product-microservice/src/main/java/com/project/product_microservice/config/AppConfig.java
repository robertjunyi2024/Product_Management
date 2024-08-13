package com.project.product_microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Value("${app.kafka-bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${app.microservice-base-url}")
    private String microserviceBaseUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public String getMicroserviceBaseUrl() {
        return microserviceBaseUrl;
    }
}
