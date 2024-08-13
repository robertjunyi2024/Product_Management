package com.project.kafka_microservice.service;

import com.project.kafka_microservice.dto.ProductDto;
import com.project.kafka_microservice.dto.ProductPageResponse;
import com.project.kafka_microservice.dto.ProductPriceDto;
import com.project.kafka_microservice.kafka.KafkaProducer;
import com.project.kafka_microservice.model.Product;
import com.project.kafka_microservice.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    /** 
     * Save the product to the database
     * @param product
     * @return Product
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Get paged products from database
     * @param page
     * @param size
     * @return
     */
    public ProductPageResponse getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        // Create a Pageable object with sorting by 'id' in ascending order
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id"))); 

        Page<Product> pageProducts = productRepository.findAll(pageable);

        List<ProductDto> productDtos = pageProducts.getContent().stream()
            .map(this::convertToProductDto)
            .collect(Collectors.toList());

        ProductPageResponse response = new ProductPageResponse();
        response.setContent(productDtos);
        response.setTotalPages(pageProducts.getTotalPages());
        response.setTotalElements(pageProducts.getTotalElements());
        response.setSize(pageProducts.getSize());
        response.setNumber(pageProducts.getNumber());

        return response;
    }

    /**
     * Convert Product to ProductDto to be transfered back
     * @param product
     * @return
     */
    private ProductDto convertToProductDto(Product product){
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        return dto;
    }

    /**
     * Check if the product already in the database by name
     * @param name
     * @return
     */
    public boolean existsByName(String name) {
        return productRepository.findByName(name).isPresent();
    }

    /**
     * Update the product price in the database
     * @param id
     * @param newPrice
     * @throws Exception
     */
    public void updateProductPrice(Long id, double newPrice) throws Exception {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new Exception("Product not found with ID: " + id));

        if (product.getPrice() == newPrice) {
            // price not changed, will not do anything
            return;
        }
        product.setPrice(newPrice);
        productRepository.save(product);

        ProductPriceDto productPriceDto = new ProductPriceDto();
        productPriceDto.setId(id);
        productPriceDto.setPrice(newPrice);
        kafkaProducer.sendPriceChange(productPriceDto);
    }

    /**
     * Delet the product in the database
     * @param id
     * @throws Exception
     */
    public void deleteProduct(Long id) throws Exception {
        productRepository.deleteById(id);
    }

    /**
     * Get the product by ID in the database
     * @param id
     * @return
     */
    public ProductDto getProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            return null;
        }

        ProductDto productDto = new ProductDto();
        productDto.setId(id);
        productDto.setName(product.get().getName());
        productDto.setPrice(product.get().getPrice());

        return productDto;
    }
}
