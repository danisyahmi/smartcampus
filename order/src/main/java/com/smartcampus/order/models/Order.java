package com.smartcampus.order.models;

import lombok.Data;

@Data
public class Order {
    private int orderId;
    private int userId;
    private String item;
    private double price;

    public Order(int orderId, int userId,
            String item, double price) {
        this.orderId = orderId;
        this.userId = userId;
        this.item = item;
        this.price = price;
    }

}
