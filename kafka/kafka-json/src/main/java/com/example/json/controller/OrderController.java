package com.example.json.controller;

import com.example.json.model.Order;
import com.example.json.service.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller to expose API endpoints for producing Kafka messages.
 *
 * Endpoint: POST /api/orders
 * Accepts: JSON payload (Order object)
 * Sends: The JSON order to Kafka topic "orders"
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderProducer orderProducer;

    // Inject the producer
    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    /**
     * Endpoint to send an Order to Kafka.
     *
     * Example:
     * POST http://localhost:8080/api/orders
     * {
     *   "orderId": "O123",
     *   "productName": "iPhone 15",
     *   "price": 79999.0,
     *   "quantity": 1,
     *   "orderTime": "2025-11-01T19:30:00"
     * }
     */
    @PostMapping
    public ResponseEntity<String> sendOrder(@RequestBody Order order) {
        logger.info("Received order request -> {}", order);

        // Send the order to Kafka
        orderProducer.sendOrder(order);

        return ResponseEntity.ok("Order sent to Kafka topic successfully!");
    }
}
