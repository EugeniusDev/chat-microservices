package com.edu.froliak.controller;

import com.edu.froliak.dto.NotificationDTO;
import com.edu.froliak.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationDTO notification) {
        kafkaProducerService.sendNotification(notification);
        return ResponseEntity.ok("Сповіщення надіслано в Kafka топік: " + notification.getType());
    }
}