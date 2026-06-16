package com.smartcampus.booking.models;

import lombok.Data;

@Data
public class Booking {
    private int bookingId;
    private int studentId;
    private String item;
    private double price;

    public Booking(int bookingId, int studentId, String item, double price) {
        this.bookingId = bookingId;
        this.studentId = studentId;
        this.item = item;
        this.price = price;
    }

}
