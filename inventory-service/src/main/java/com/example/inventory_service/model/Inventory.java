package com.example.inventory_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private Long quantity;
    private Double price;

}
