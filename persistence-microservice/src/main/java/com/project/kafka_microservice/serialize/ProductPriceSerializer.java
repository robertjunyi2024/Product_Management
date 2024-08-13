package com.project.kafka_microservice.serialize;

import java.io.IOException;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kafka_microservice.dto.ProductPriceDto;

public class ProductPriceSerializer implements Serializer<ProductPriceDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, ProductPriceDto data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing Product object to JSON", e);
        }
    }
}