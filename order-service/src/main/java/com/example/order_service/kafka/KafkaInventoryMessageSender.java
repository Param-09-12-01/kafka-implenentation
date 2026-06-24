package com.example.order_service.kafka;

import com.example.order_service.dto.InventoryEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaInventoryMessageSender {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(KafkaInventoryMessageSender.class);

    public void sendMessage(String topicName, InventoryEvent event) {

        LOG.info("Sending inventory message to topic: {}, payload: {}", topicName, event);

        kafkaTemplate.send(topicName, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        LOG.info(
                                "Message sent successfully. Topic: {}, Partition: {}, Offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    } else {
                        LOG.error(
                                "Failed to send message to topic: {}. Payload: {}",
                                topicName,
                                event,
                                ex
                        );
                    }
                });
    }
}