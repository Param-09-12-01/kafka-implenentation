package com.example.inventory_service.controller;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    public void saveInventory(@RequestBody Inventory inventory) {
        inventoryService.saveInventoryRecord(inventory);
    }

    @GetMapping("/knownException")
    public void throwKnownException() {
        inventoryService.throwNoSuchElementException();
    }

    @GetMapping("/unknownException")
    public void unknownException() throws Exception {
        inventoryService.throwRandomTestException();
    }
}
