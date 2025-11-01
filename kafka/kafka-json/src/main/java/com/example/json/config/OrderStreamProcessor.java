package com.example.json.config;

import com.example.json.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class OrderStreamProcessor {

    //Proper ObjectMapper with LocalDateTime support
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Bean
    public KafkaStreams kafkaStreams() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        // Stream from "orders" topic
        KStream<String, String> orders = builder.stream("orders");

        // Filter orders based on price > 10000
        KStream<String, String> highValueOrders = orders.filter((key, value) -> {
            try {
                Order order = mapper.readValue(value, Order.class);
                return order.getPrice() > 10000;
            } catch (Exception e) {
                System.err.println("Failed to parse message: " + e.getMessage());
                return false;
            }
        });

        // Output to high-value-orders topic
        highValueOrders.to("high-value-orders", Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        return streams;
    }
}
