package com.example.admin_service.service;

import com.example.admin_service.kafka.KafkaAdminNotificationSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminNotificationService.class);

    private final KafkaAdminNotificationSender kafkaAdminNotificationSender;

    public String sendAdminNotification(String message) {
        if (!StringUtils.hasText(message)) {
            LOG.warn("Rejected blank admin notification request");
            throw new IllegalArgumentException("Admin notification message must not be blank");
        }

        String trimmedMessage = message.trim();
        kafkaAdminNotificationSender.sendMessage(trimmedMessage);
        LOG.info("Admin notification publish request accepted. messageLength: {}", trimmedMessage.length());
        return "Admin notification published successfully";
    }

    public void throwRandomTestException() throws Exception {
        List<Class<? extends Exception>> exceptions = List.of(
                IllegalArgumentException.class,
                IllegalStateException.class,
                NullPointerException.class,
                UnsupportedOperationException.class,
                RuntimeException.class,
                ArithmeticException.class,
                IndexOutOfBoundsException.class,
                ClassCastException.class
        );

        Class<? extends Exception> randomClass = exceptions.get(ThreadLocalRandom.current().nextInt(exceptions.size()));
        LOG.warn("Throwing random admin test exception. exceptionType: {}", randomClass.getSimpleName());
        Constructor<? extends Exception> constructor = randomClass.getConstructor(String.class);
        throw constructor.newInstance("Random exception from " + randomClass.getSimpleName());
    }
}
