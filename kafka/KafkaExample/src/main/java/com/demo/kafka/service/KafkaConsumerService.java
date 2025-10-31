package com.demo.kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final String DLQ_TOPIC = "demo-topic-dlq";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "demo-topic", groupId = "demo-group")
    public void consume(String message, Acknowledgment ack) {
        try {
            System.out.println("Received: " + message);

            // Simulate failure for demo
            if (message.contains("fail")) {
                throw new RuntimeException("Processing failed for message: " + message);
            }

            // Processing succeeded, commit offset
            ack.acknowledge();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());

            // Send to Dead Letter Queue
            System.out.println("Sending message to "+ DLQ_TOPIC);
            kafkaTemplate.send(DLQ_TOPIC, message);

            // Commit offset to avoid retrying poisoned message
            ack.acknowledge();
        }
    }
}