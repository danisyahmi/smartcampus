package com.smartcampus.notification.payment.dto;

public class PaymentRequest {
    private String bookingType;   // "ROOM" atau "BOOK"
    private Long bookingId;
    private Double amount;        // Optional — auto-kira jika kosong (ROOM=RM5, BOOK=RM3)
    private String matricNo;
    private boolean isSuccessSimulation;

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMatricNo() { return matricNo; }
    public void setMatricNo(String matricNo) { this.matricNo = matricNo; }

    public boolean isSuccessSimulation() { return isSuccessSimulation; }
    public void setSuccessSimulation(boolean successSimulation) { this.isSuccessSimulation = successSimulation; }
}