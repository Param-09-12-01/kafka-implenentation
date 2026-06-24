package com.example.order_service.service;

import com.example.order_service.dto.InventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryService {

    private final RestTemplate inventoryRestTemplate;

    public InventoryService(@Qualifier("inventoryRestTemplate") RestTemplate inventoryRestTemplate) {
        this.inventoryRestTemplate = inventoryRestTemplate;
    }

    public InventoryDto getProductByName(String productName) {
        return inventoryRestTemplate.getForObject("/api/inventory/{productName}", InventoryDto.class, productName);
    }

}
