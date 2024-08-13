package com.project.kafka_microservice.serialize;

import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kafka_microservice.dto.ProductDto;

public class ProductCreateDeserializer implements Deserializer<ProductDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductDto deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(data, ProductDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing Product object from JSON", e);
        }
    }
}