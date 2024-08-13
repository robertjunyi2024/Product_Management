package com.project.kafka_microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.kafka_microservice.dto.ProductDto;
import com.project.kafka_microservice.dto.ProductPageResponse;
import com.project.kafka_microservice.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class PersistenceController {
    @Autowired
    private ProductService productService;

    /** 
     * Getting paged products
     * @param page
     * @param size
     * @return ProductPageResponse
     */
    @GetMapping
    public ProductPageResponse getProducts(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        return productService.getProducts(page, size);
    }

    /** 
     * Get product by ID
     * @param id
     * @return ProductDto
     */
    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    /**
     * Check if product with name exists 
     * @param name
     * @return ResponseEntity<Boolean>
     */    
    @GetMapping("/exists-by-name")
    public ResponseEntity<Boolean> checkProductExistsByName(@RequestParam String name) {
        if (productService.existsByName(name)) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        }
        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
    }
}
