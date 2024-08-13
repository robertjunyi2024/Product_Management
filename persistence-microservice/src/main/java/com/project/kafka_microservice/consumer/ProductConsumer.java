package com.project.kafka_microservice.consumer;

import com.project.kafka_microservice.dto.ProductDto;
import com.project.kafka_microservice.dto.ProductPriceDto;
import com.project.kafka_microservice.model.Product;
import com.project.kafka_microservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductConsumer {

    @Autowired
    private ProductService productService;

    @KafkaListener(topics = "product_create_topic", groupId = "product_create_group",
        containerFactory = "productCreateKafkaListenerContainerFactory")
    public void consumeProductCreation(ProductDto productDto) throws Exception{
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        productService.saveProduct(product);
    }

    @KafkaListener(topics = "product_price_topic", groupId = "product_price_group",
        containerFactory = "productPriceKafkaListenerContainerFactory")
    public void consumeProductPriceUpdate(ProductPriceDto productPriceDto) throws Exception{
        Product product = new Product();
        product.setId(productPriceDto.getId());
        product.setPrice(productPriceDto.getPrice());
        productService.updateProductPrice(productPriceDto.getId(), productPriceDto.getPrice());
    }

    @KafkaListener(topics = "product_delete_topic", groupId = "product_delete_group",
     containerFactory = "longKafkaListenerContainerFactory")
    public void consumeProductDelete(Long id) throws Exception {
        productService.deleteProduct(id);
    }
}
