package com.example.inventory_service.kafka;

import com.example.inventory_service.dto.FailedSvcLogEvent;
import com.example.inventory_service.dto.InventoryNotificationEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public static final String FAILED_SERVICE_LOG_KAFKA_TEMPLATE = "failedServiceLogKafkaTemplate";

    @Bean
    public ProducerFactory<String, InventoryNotificationEvent> inventoryNotificationProducerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, InventoryNotificationEvent> inventoryNotificationKafkaTemplate() {
        return new KafkaTemplate<>(inventoryNotificationProducerFactory());
    }


    @Bean
    public ProducerFactory<String, FailedSvcLogEvent> failedServiceLogProducerFactory() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean(FAILED_SERVICE_LOG_KAFKA_TEMPLATE)
    public KafkaTemplate<String, FailedSvcLogEvent> failedServiceLogKafkaTemplate() {
        return new KafkaTemplate<>(failedServiceLogProducerFactory());
    }

}
