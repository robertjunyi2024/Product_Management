package com.project.kafka_microservice.dto;

import java.io.Serializable;
import java.util.List;

public class ProductPageResponse implements Serializable {
    private List<ProductDto> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;

    public List<ProductDto> getContent() {
        return content;
    }
    public void setContent(List<ProductDto> content) {
        this.content = content;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
