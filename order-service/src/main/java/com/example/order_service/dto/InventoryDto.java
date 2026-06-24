package com.example.order_service.dto;


import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InventoryDto {
    private Long id;
    private String productName;
    private Long quantity;
    private Double price;
}
