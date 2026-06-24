package com.example.order_service.service;

import com.example.order_service.dto.InventoryEvent;
import com.example.order_service.kafka.InventoryTopicConstant;
import com.example.order_service.kafka.KafkaInventoryMessageSender;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaInventoryMessageSender kafkaInventoryMessageSender;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order saveOrder(Order order) {
        order = orderRepository.save(order);
        kafkaInventoryMessageSender.sendMessage(InventoryTopicConstant.ORDER_CREATED, createInventoryEvent(order));
        return order;
    }

    private InventoryEvent createInventoryEvent(Order order) {
        return InventoryEvent.builder()
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .build();
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}