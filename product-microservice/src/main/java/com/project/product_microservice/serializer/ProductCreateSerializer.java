package com.project.product_microservice.serializer;

import java.io.IOException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.product_microservice.dto.ProductDto;


public class ProductCreateSerializer implements Serializer<ProductDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, ProductDto data) {
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