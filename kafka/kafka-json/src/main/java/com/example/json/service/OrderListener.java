package com.example.json.service;

import com.example.json.model.Order;
import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;


/**
 * Kafka consumer that listens to the "orders" topic.
 *
 * Whenever a new message is published to the topic,
 * this class automatically receives it and processes it.
 */
@Service
public class OrderListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderListener.class);

    /**
     * Listens to the "orders" topic and consumes messages as Order objects.
     *
     * The @KafkaListener annotation automatically deserializes the JSON message
     * into an Order object (based on spring.json.trusted.packages in application.yml).
     */

    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);

    private final Cache<String, Boolean> processedOrders = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)   // auto-remove after 1 minute
            .maximumSize(100_000)  //it represents the maximum number of entries (elements) the cache will hold.
            .build();

    @KafkaListener(topics = "orders", groupId = "order-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(Order order) {
        String id = order.getOrderId();
        if (processedOrders.getIfPresent(id) != null) {
            log.warn("Duplicate order detected -> {}", id);
            return;
        }
        processOrder(order);
        processedOrders.put(id, Boolean.TRUE);
        log.info("Processed new order -> {}", id);
    }

    @KafkaListener(topics = "high-value-orders", groupId = "order-stream-group")
    public void consumeHighValue(Order order) {
        String id = order.getOrderId();
        processOrder(order);
        log.info("Processed new high value order -> {}", id);
    }

    /**
     * Example processing method that could be replaced with real business logic.
     */
    private void processOrder(Order order) {
        logger.info("Processing Order: {} | Product: {} | Total Amount: {}",
                order.getOrderId(),
                order.getProductName(),
                order.getPrice() * order.getQuantity());
    }
}
