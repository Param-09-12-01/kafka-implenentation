package com.example.inventory_service.service;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);
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
                    LOG.info("Reserved inventory stock. productName: {}, orderedQuantity: {}, remainingQuantity: {}",
                            productName, orderedQuantity, inventory.getQuantity());
                    return true;
                })
                .orElseGet(() -> {
                    LOG.warn("Unable to reserve inventory stock. productName: {}, orderedQuantity: {}",
                            productName, orderedQuantity);
                    return false;
                });
    }

    public void saveInventoryRecord(Inventory inventory) {
        inventoryRepository.save(inventory);
        LOG.info("Saved inventory record. productName: {}, quantity: {}, price: {}",
                inventory.getProductName(), inventory.getQuantity(), inventory.getPrice());
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
        LOG.info("Deleted inventory record. id: {}", id);
    }

    public void throwNoSuchElementException() {
        throw new NoSuchElementException("Inventory record does not exist");
    }

    public void throwRandomTestException() throws Exception {
        List<Class<? extends Exception>> exceptions = List.of(
                IllegalArgumentException.class,
                IllegalStateException.class,
                NullPointerException.class,
                UnsupportedOperationException.class,
                RuntimeException.class,
                ArithmeticException.class,
                IndexOutOfBoundsException.class,
                ClassCastException.class
        );

        Class<? extends Exception> randomClass = exceptions.get(ThreadLocalRandom.current().nextInt(exceptions.size()));
        LOG.warn("Throwing random inventory test exception. exceptionType: {}", randomClass.getSimpleName());
        Constructor<? extends Exception> constructor = randomClass.getConstructor(String.class);
        throw constructor.newInstance("Random exception from " + randomClass.getSimpleName());
    }
}
