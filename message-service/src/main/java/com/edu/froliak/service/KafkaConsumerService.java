package com.edu.froliak.service;

import com.edu.froliak.config.KafkaConfig;
import com.edu.froliak.dto.NotificationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaConfig.CHAT_NOTIFICATIONS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenChatNotifications(@Payload NotificationDTO notification) {
        log.info("<<< ОТРИМАНО ПОВІДОМЛЕННЯ З KAFKA ({}): {} >>>",
                KafkaConfig.CHAT_NOTIFICATIONS_TOPIC, notification);

        System.out.println("Received Kafka Message: " + notification.toString());
    }
}