package com.example.inventory.dto;

import com.example.inventory.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Data the API sends back. Entity is never exposed directly. */
public record ProductResponse(Long id, String name, String description, BigDecimal price, Integer stockQuantity) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice().setScale(2, RoundingMode.HALF_UP),
                p.getStockQuantity());
    }
}
