package com.example.json.service;

import com.example.json.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * OrderProducer is responsible for publishing Order messages to the "orders" topic.
 *
 * It uses KafkaTemplate (auto-configured by Spring Boot) to send messages.
 * Since we configured JsonSerializer in application.yml,
 * the Order object will automatically be converted into JSON.
 */
@Service
public class OrderProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);

    // KafkaTemplate is injected automatically by Spring Boot
    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends the given order to the "orders" Kafka topic.
     *
     * @param order the order object to send
     */
    public void sendOrder(Order order) {
        // Topic name
        String topic = "orders";

        logger.info("Sending order to Kafka -> {}", order);

        // Send the order object asynchronously
        kafkaTemplate.send(topic, order)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("Order sent successfully to topic '{}', partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        logger.error("Failed to send order to Kafka: {}", ex.getMessage(), ex);
                    }
                });
    }
}
