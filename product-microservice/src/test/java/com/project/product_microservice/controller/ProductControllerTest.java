package com.project.product_microservice.controller;

import com.project.product_microservice.dto.ProductDto;
import com.project.product_microservice.dto.ProductPageResponse;
import com.project.product_microservice.dto.ProductPriceDto;
import com.project.product_microservice.dto.UpdatePriceRequest;
import com.project.product_microservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct_WhenProductExists() {
        ProductDto productDto = new ProductDto();
        productDto.setName("Existing Product");

        when(productService.getProductByName(any(String.class))).thenReturn(true);

        ResponseEntity<String> response = productController.createProduct(productDto);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("Product with this name already exists", response.getBody());
    }

    @Test
    void testCreateProduct_WhenProductDoesNotExist() {
        ProductDto productDto = new ProductDto();
        productDto.setName("New Product");

        when(productService.getProductByName(any(String.class))).thenReturn(false);

        ResponseEntity<String> response = productController.createProduct(productDto);

        verify(productService, times(1)).sendProductToKafka(productDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product sent to Kafka for creation.", response.getBody());
    }

    @Test
    void testGetProducts() {
        ProductPageResponse response = new ProductPageResponse();
        when(productService.getProducts(anyInt(), anyInt())).thenReturn(response);

        ResponseEntity<ProductPageResponse> result = productController.getProducts(0, 10);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void testUpdateProductPrice() {
        UpdatePriceRequest updatePriceRequest = new UpdatePriceRequest();
        updatePriceRequest.setPrice(100.0);

        ProductPriceDto expectedProductPriceDto = new ProductPriceDto();
        expectedProductPriceDto.setId(1L);
        expectedProductPriceDto.setPrice(100.0);

        ResponseEntity<String> response = productController.updateProductPrice(1L, updatePriceRequest);

        verify(productService).sendProductPriceToKafka(argThat(productPriceDto -> 
            productPriceDto.getId().equals(expectedProductPriceDto.getId()) &&
            productPriceDto.getPrice().equals(expectedProductPriceDto.getPrice())
        ));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product price sent to Kafka for update.", response.getBody());
    }

    @Test
    void testDeleteProduct() {
        ResponseEntity<String> response = productController.deleteProduct(1L);

        verify(productService, times(1)).sendProductDeleteToKafka(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product sent to Kafka for deletion.", response.getBody());
    }

    @Test
    void testGetProduct() {
        ProductDto productDto = new ProductDto();
        when(productService.getProduct(anyLong())).thenReturn(productDto);

        ResponseEntity<ProductDto> response = productController.getProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDto, response.getBody());
    }

    @Test
    void testGetProductPriceUpdates() {
        ProductPriceDto productPriceDto = new ProductPriceDto();
        Flux<ProductPriceDto> flux = Flux.just(productPriceDto);

        when(productService.getProductPriceUpdates()).thenReturn(flux);

        Flux<ProductPriceDto> result = productController.getProductPriceUpdates();

        result.collectList().subscribe(list -> {
            assertEquals(1, list.size());
            assertEquals(productPriceDto, list.get(0));
        });
    }
}
