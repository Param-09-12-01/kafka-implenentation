package com.example.order_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaInventoryTopic {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(InventoryTopicConstant.ORDER_CREATED).build();
    }

    @Bean
    public NewTopic failedServiceLogTopic() {
        return TopicBuilder.name(InventoryTopicConstant.FAILED_SERVICE_LOG_TOPIC).build();
    }
}
