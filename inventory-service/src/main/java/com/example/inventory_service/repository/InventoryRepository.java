package com.example.inventory_service.repository;

import com.example.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    Optional<Inventory> findByProductName(String productName);

    @Query("select case when count(i) > 0 then true else false end from Inventory i where i.productName = :productName and i.quantity >= :orderedQuantity")
    boolean isStockAvailable(@Param("productName") String productName, @Param("orderedQuantity") Long orderedQuantity);

}
