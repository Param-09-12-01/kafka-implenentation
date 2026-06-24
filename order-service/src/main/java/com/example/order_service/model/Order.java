package com.example.order_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private Double amount;
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}