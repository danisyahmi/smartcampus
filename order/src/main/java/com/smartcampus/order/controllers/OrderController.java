package com.smartcampus.order.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.smartcampus.order.models.Order;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    // USER_SERVICE_URL is injected from environment variable
    private final String userServiceUrl = System.getenv().getOrDefault(
            "USER_SERVICE_URL",
            "http://localhost:8081");

    private final List<Order> orders = new ArrayList<>(List.of(
            new Order(101, 1, "Java Programming Book", 89.90),
            new Order(102, 2, "Wireless Mouse", 45.00),
            new Order(103, 1, "USB-C Hub", 120.00)));

    @GetMapping
    public List<Order> getAll() {
        return orders;
    }

    @GetMapping("/user/{userId}")
    public Map<String, Object> getOrdersForUser(
            @PathVariable int userId) {
        // Call User Service to verify user exists
        RestTemplate rt = new RestTemplate();
        Object user = rt.getForObject(
                userServiceUrl + "/api/users/" + userId,
                Object.class);

        List<Order> userOrders = orders.stream()
                .filter(o -> o.getUserId() == userId)
                .toList();

        return Map.of(
                "user", user,
                "orders", userOrders);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "order-service",
                "status", "UP",
                "port", "8084");
    }
}
