package com.project.kafka_microservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.project.kafka_microservice.config.AppConfig;
import com.project.kafka_microservice.dto.ProductPriceDto;

@Service
public class KafkaProducer {
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private KafkaTemplate<String, ProductPriceDto> kafkaProductPriceTemplate;

    public void sendPriceChange(ProductPriceDto productPrice) {
        kafkaProductPriceTemplate.send(appConfig.getProductPriceChangesTopic(), productPrice);
    }

}
