package com.project.kafka_microservice.controller;

import com.project.kafka_microservice.dto.ProductDto;
import com.project.kafka_microservice.dto.ProductPageResponse;
import com.project.kafka_microservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PersistenceControllerTest {

    @InjectMocks
    private PersistenceController persistenceController;

    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProducts() {
        ProductPageResponse mockResponse = new ProductPageResponse();
        when(productService.getProducts(anyInt(), anyInt())).thenReturn(mockResponse);

        ProductPageResponse response = persistenceController.getProducts(0, 10);

        assertNotNull(response);
        assertEquals(mockResponse, response);
        verify(productService, times(1)).getProducts(0, 10);
    }

    @Test
    void testGetProduct() {
        ProductDto mockProductDto = new ProductDto();
        when(productService.getProduct(anyLong())).thenReturn(mockProductDto);

        ProductDto response = persistenceController.getProduct(1L);

        assertNotNull(response);
        assertEquals(mockProductDto, response);
        verify(productService, times(1)).getProduct(anyLong());
    }

    @Test
    void testCheckProductExistsByNameExists() {
        when(productService.existsByName(anyString())).thenReturn(true);

        ResponseEntity<Boolean> response = persistenceController.checkProductExistsByName("ProductName");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(productService, times(1)).existsByName(anyString());
    }

    @Test
    void testCheckProductExistsByNameDoesNotExist() {
        when(productService.existsByName(anyString())).thenReturn(false);

        ResponseEntity<Boolean> response = persistenceController.checkProductExistsByName("NonExistentProduct");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(productService, times(1)).existsByName(anyString());
    }
}
