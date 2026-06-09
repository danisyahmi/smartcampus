package com.smartcampus.order.models;

import lombok.Data;

@Data
public class Order {
    private int orderId;
    private int studentId;
    private String item;
    private double price;

    public Order(int orderId, int studentId, String item, double price) {
        this.orderId = orderId;
        this.studentId = studentId;
        this.item = item;
        this.price = price;
    }

}
