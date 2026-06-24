package com.example.inventory_service.service;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public boolean checkStockAvailable(String productName, Long orderedQuantity) {
        return inventoryRepository.isStockAvailable(productName, orderedQuantity);
    }

    @Transactional
    public boolean reserveStock(String productName, Long orderedQuantity) {
        return inventoryRepository.findByProductName(productName)
                .filter(inventory -> inventory.getQuantity() >= orderedQuantity)
                .map(inventory -> {
                    inventory.setQuantity(inventory.getQuantity() - orderedQuantity);
                    inventoryRepository.save(inventory);
                    return true;
                })
                .orElse(false);
    }

    public void saveInventoryRecord(Inventory inventory) {
        inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}
