package com.example.notification_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic inventoryNotificationTopic() {
        return TopicBuilder.name(NotificationTopicConstant.INVENTORY_NOTIFICATION)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
