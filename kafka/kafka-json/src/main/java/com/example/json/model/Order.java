package com.example.json.model;

import java.time.LocalDateTime;

/**
 * Represents an Order object that will be serialized to JSON and sent to Kafka.
 *
 * When you send this object through KafkaTemplate, it is automatically converted
 * into a JSON message using JsonSerializer (configured in application.yml).
 *
 * Similarly, when the consumer receives it, Spring Kafka automatically
 * deserializes the JSON back into an Order object.
 */
public class Order {

    private String orderId;
    private String productName;
    private double price;
    private int quantity;
    private LocalDateTime orderTime;

    public Order() {
        // Default constructor required for deserialization
    }

    public Order(String orderId, String productName, double price, int quantity, LocalDateTime orderTime) {
        this.orderId = orderId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.orderTime = orderTime;
    }

    // ===========================
    // 🔹 Getters & Setters
    // ===========================

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    // ===========================
    // 🔹 toString (for logging)
    // ===========================
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", orderTime=" + orderTime +
                '}';
    }
}
