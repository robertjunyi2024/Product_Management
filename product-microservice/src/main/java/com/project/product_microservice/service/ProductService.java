package com.project.product_microservice.service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.project.product_microservice.config.AppConfig;
import com.project.product_microservice.dto.ProductDto;
import com.project.product_microservice.dto.ProductPageResponse;
import com.project.product_microservice.dto.ProductPriceDto;
import com.project.product_microservice.kafka.KafkaProducer;
import com.project.product_microservice.serializer.ProductPriceDeserializer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ProductService {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppConfig appConfig;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /** 
     * create a new instance of Kafka consumer
     * @return KafkaConsumer<String, ProductPriceDto>
     */
    public KafkaConsumer<String, ProductPriceDto> createKafkaConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getKafkaBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "product_price_change_group_" + UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ProductPriceDeserializer.class);
        return new KafkaConsumer<>(props);
    }

    /** 
     * Send ProductDto to kafka
     * @param product
     */
    public void sendProductToKafka(ProductDto product) {
        kafkaProducer.sendProductSaveMessage(product);
    }

    /** 
     * Send ProductPriceDto to kafka
     * @param productPrice
     */
    public void sendProductPriceToKafka(ProductPriceDto productPrice) {
        kafkaProducer.sendProductPriceUpdateMessage(productPrice);
    }

    /** 
     * Send product ID to kafka for deletion
     * @param id
     */
    public void sendProductDeleteToKafka(Long id) {
        kafkaProducer.sendProductDeleteMessage(id);
    }

    /** 
     * Retrieve the products by page
     * This use restTemplate to communicate to another microservice which
     * deals with database layer to retrieve the paged records
     * @param page
     * @param size
     * @return ProductPageResponse
     */
    public ProductPageResponse getProducts(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        String url = UriComponentsBuilder.fromHttpUrl(appConfig.getMicroserviceBaseUrl())
        .queryParam("page", page).queryParam("size", size)
        .toUriString();

        ResponseEntity<ProductPageResponse> response = restTemplate.exchange(
            url, HttpMethod.GET,
            null, new ParameterizedTypeReference<ProductPageResponse>() {});

        return  response.getBody();
    }

    /** 
     * Retrieve the product details
     * This use the restTemplate to communicate to another microservice which
     * detals with database layer to retrieve the product details by ID
     * @param id
     * @return ProductDto
     */
    public ProductDto getProduct(@PathVariable Long id) {
        String url = UriComponentsBuilder.fromHttpUrl(appConfig.getMicroserviceBaseUrl() + "/" + id).toUriString();
        ResponseEntity<ProductDto> response = restTemplate.exchange(
            url, HttpMethod.GET,
            null, new ParameterizedTypeReference<ProductDto>() {});

        return  response.getBody();
    }

    /** 
     * Check if the product already exists by name
     * This use the restTemplate to communicate to another microservice which
     * detals with database layer to check the product existence by name
     * @param name
     * @return Boolean
     */
    public Boolean getProductByName(String name) {
        // Construct the URL with query parameter
        String url = UriComponentsBuilder.fromHttpUrl(appConfig.getMicroserviceBaseUrl() + "/exists-by-name")
            .queryParam("name", name)
            .toUriString();

        ResponseEntity<Boolean> response = restTemplate.exchange(
            url, HttpMethod.GET,
            null, new ParameterizedTypeReference<Boolean>() {});

        return response.getBody();
    }
    
    /** 
     * Subscribe a consumer to "product-price-changes" so if there is a price change, it will
     * be notified and send the record to client.
     * Each client registers its own consumer to "product-price-changes" because
     * ConsumerConfig.GROUP_ID_CONFIG is unqiute per request, so if there is a price change,
     * all client will be notified
     * @return Flux<ProductPriceDto>
     */
    public Flux<ProductPriceDto> getProductPriceUpdates() {
        Sinks.Many<ProductPriceDto> sink = Sinks.many().multicast().onBackpressureBuffer();
        KafkaConsumer<String, ProductPriceDto> kafkaConsumer = createKafkaConsumer();
        kafkaConsumer.subscribe(Collections.singletonList("product-price-changes"));

        executorService.scheduleAtFixedRate(() -> {
            try {
                var records = kafkaConsumer.poll(Duration.ofMillis(1000));
                records.forEach(record -> sink.tryEmitNext(record.value()));
            } catch (Exception e) {
                sink.tryEmitError(e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        return sink.asFlux().doFinally(signal -> {
            System.out.println("cleaning up kafka...");
            kafkaConsumer.close(); // Ensure the Kafka consumer is closed properly
        });
    }
}