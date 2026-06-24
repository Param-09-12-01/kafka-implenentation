package com.example.order_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaInventoryTopic {

    @Bean
    public NewTopic toInventoryService() {
        return TopicBuilder.name(InventoryTopicConstant.ORDER_CREATED).build();
    }
}
