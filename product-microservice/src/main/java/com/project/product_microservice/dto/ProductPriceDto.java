package com.project.product_microservice.dto;

import java.io.Serializable;

public class ProductPriceDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Double price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}