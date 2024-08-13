package com.project.kafka_microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${app.product-price-changes-topic}")
    private String productPriceChangesTopic;

    public String getProductPriceChangesTopic() {
        return productPriceChangesTopic;
    }
}

