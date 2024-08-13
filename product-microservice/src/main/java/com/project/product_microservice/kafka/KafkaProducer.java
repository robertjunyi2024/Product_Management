package com.project.product_microservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.project.product_microservice.dto.ProductDto;
import com.project.product_microservice.dto.ProductPriceDto;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, ProductDto> kafkaProductTemplate;

    @Autowired
    private KafkaTemplate<String, ProductPriceDto> kafkaProductPriceTemplate;

    @Autowired
    private KafkaTemplate<String, Long> kafkaProductDeleteTemplate;

    private static final String PRODUCT_CREATE_TOPIC = "product_create_topic";
    private static final String PRODUCT_PRICE_TOPIC = "product_price_topic";
    private static final String PRODUCT_DELETE_TOPIC = "product_delete_topic";

    public void sendProductSaveMessage(ProductDto product) {
        kafkaProductTemplate.send(PRODUCT_CREATE_TOPIC, product);
    }

    public void sendProductPriceUpdateMessage(ProductPriceDto productPrice) {
        kafkaProductPriceTemplate.send(PRODUCT_PRICE_TOPIC, productPrice);
    }

    public void sendProductDeleteMessage(Long id) {
        kafkaProductDeleteTemplate.send(PRODUCT_DELETE_TOPIC, id);
    }
}