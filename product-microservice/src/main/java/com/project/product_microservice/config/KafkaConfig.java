package com.project.product_microservice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import com.project.product_microservice.dto.ProductDto;
import com.project.product_microservice.dto.ProductPriceDto;
import com.project.product_microservice.serializer.ProductCreateSerializer;
import com.project.product_microservice.serializer.ProductPriceSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";

    @Bean
    public KafkaTemplate<String, ProductDto> kafkaProductTemplate() {
        return new KafkaTemplate<>(productProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, ProductPriceDto> kafkaProductPriceTemplate() {
        return new KafkaTemplate<>(productPriceProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, Long> kafkaProductDeleteTemplate() {
        return new KafkaTemplate<>(productDeleteProducerFactory());
    }

    @Bean
    public ProducerFactory<String, ProductDto> productProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ProductCreateSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, ProductPriceDto> productPriceProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ProductPriceSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, Long> productDeleteProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);    }

}