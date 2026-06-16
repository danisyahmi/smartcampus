package com.library_system;

import jakarta.jws.WebService;
import java.util.UUID;

@WebService(endpointInterface = "com.library_system.LibraryService")
public class LibraryServiceImpl implements LibraryService {

    @Override
    public String loanBook(String bookId, String studentId) {
        System.out.println("SOAP Request: Processing loan for Book ID: " + bookId + " to Student: " + studentId);
        
        String loanReference = "LNX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "SUCCESS|Loan Approved. Reference: " + loanReference + ". Due in 14 days.";
    }

    @Override
    public String reserveDiscussionRoom(String roomId, String studentId, String bookingDate) {
        System.out.println("SOAP Request: Processing room reservation for Room: " + roomId + " on " + bookingDate);
        
        String receiptId = "REC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "SUCCESS|Room " + roomId + " reserved for Student " + studentId + " on " + bookingDate + ". Receipt: " + receiptId;
    }
}