package com.project.product_microservice.serializer;

import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.product_microservice.dto.ProductPriceDto;

public class ProductPriceDeserializer implements Deserializer<ProductPriceDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductPriceDto deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(data, ProductPriceDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing Product object from JSON", e);
        }
    }
}