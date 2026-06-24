package com.example.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InventoryEvent {
    private String productName;
    private Long quantity;
}