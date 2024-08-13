package com.project.product_microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.product_microservice.dto.ProductDto;
import com.project.product_microservice.dto.ProductPageResponse;
import com.project.product_microservice.dto.ProductPriceDto;
import com.project.product_microservice.dto.UpdatePriceRequest;
import com.project.product_microservice.service.ProductService;

import reactor.core.publisher.Flux;

@CrossOrigin(origins = {"*"}, allowedHeaders = "*", methods = {RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.GET})
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Register for a new product
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productDto) {
        Boolean productExists = productService.getProductByName(productDto.getName());
        if (productExists) {
            return new ResponseEntity<>("Product with this name already exists", HttpStatus.FOUND);
        }
        productService.sendProductToKafka(productDto);
        return ResponseEntity.ok("Product sent to Kafka for creation.");
    }

    // Getting paged products
    @GetMapping
    public ResponseEntity<ProductPageResponse> getProducts(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
        ProductPageResponse response = productService.getProducts(page, size);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/update-price")
    public ResponseEntity<String> updateProductPrice(@PathVariable Long id, @RequestBody UpdatePriceRequest updatePriceRequest) {
        ProductPriceDto productPrice = new ProductPriceDto();
        productPrice.setId(id);
        productPrice.setPrice(updatePriceRequest.getPrice());
        productService.sendProductPriceToKafka(productPrice);
        return ResponseEntity.ok("Product price sent to Kafka for update.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.sendProductDeleteToKafka(id);
        return ResponseEntity.ok("Product sent to Kafka for deletion.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        ProductDto productDto = productService.getProduct(id);
        return ResponseEntity.ok(productDto);
    }

    // This method returns Flux to client so client gets real time update on the server change
    @GetMapping(value = "/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductPriceDto> getProductPriceUpdates() {
        return productService.getProductPriceUpdates();
    }
}