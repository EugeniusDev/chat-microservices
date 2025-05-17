package com.edu.froliak.service;

import com.edu.froliak.config.KafkaConfig;
import com.edu.froliak.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendNotification(NotificationDTO notification) {
        log.info("Надсилання сповіщення в Kafka: {}", notification);
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(KafkaConfig.CHAT_NOTIFICATIONS_TOPIC, notification);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Повідомлення успішно надіслано в топік {} з offset {} та partition {}",
                            KafkaConfig.CHAT_NOTIFICATIONS_TOPIC,
                            result.getRecordMetadata().offset(),
                            result.getRecordMetadata().partition());
                } else {
                    log.error("Помилка надсилання повідомлення в Kafka: {}", ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Виняток під час надсилання в Kafka: {}", e.getMessage(), e);
        }
    }
}