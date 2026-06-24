package com.example.admin_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic adminNotificationTopic() {
        return TopicBuilder.name(KafkaConstant.ADMIN_NOTIFICATION_TOPIC).build();
    }

    @Bean
    public NewTopic failedServiceLogTopic() {
        return TopicBuilder.name(KafkaConstant.FAILED_SERVICE_LOG_TOPIC).build();
    }
}
