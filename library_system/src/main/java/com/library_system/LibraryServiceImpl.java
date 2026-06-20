package com.library_system;

import jakarta.jws.WebService;
import java.util.UUID;

@WebService(endpointInterface = "com.library_system.LibraryService")
public class LibraryServiceImpl implements LibraryService {

    // Book loan operation
    @Override
    public String loanBook(String bookId, String studentId) {
        System.out.println("SOAP Request: Processing loan for Book ID: " + bookId + " to Student: " + studentId);

        // SOAP fault triggers check
        if ("FAULT_TEST".equalsIgnoreCase(studentId)) {
            try {
                this.checkStudentEligibility(studentId);
            } catch (LibraryFaultException e) {
                // Runtime execution throw ensures JAX-WS captures and marshals this into a
                // <soap:Fault>
                throw new RuntimeException(e.getMessage());
            }
        }

        String loanReference = "LNX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "SUCCESS|Loan Approved. Reference: " + loanReference + ". Due in 14 days.";
    }

    @Override
    public String returnBook(String bookId, String studentId) {
        System.out.println("SOAP Request: Returning Book ID: " + bookId + " from Student: " + studentId);
        return "SUCCESS|Book ID " + bookId
                + " has been successfully checked back into the legacy catalog storage layer.";
    }

    // Discussion room reservation operation
    @Override
    public String reserveDiscussionRoom(String roomId, String studentId, String bookingDate) {
        System.out.println("SOAP Request: Processing room reservation for Room: " + roomId + " on " + bookingDate);

        // R8 SOAP Fault Triggers Check
        if ("FAULT_TEST".equalsIgnoreCase(studentId)) {
            try {
                checkStudentEligibility(studentId);
            } catch (LibraryFaultException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        String receiptId = "REC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "SUCCESS|Room " + roomId + " reserved for Student " + studentId + " on " + bookingDate + ". Receipt: "
                + receiptId;
    }

    @Override
    public String cancelRoomReservation(String bookingId) {
        System.out.println("SOAP Request: Cancelling transaction tracking reference reservation: " + bookingId);
        return "SUCCESS|Reservation reference ID " + bookingId
                + " has been cleanly stripped from the legacy mainframe allocations.";
    }

    @Override
    public boolean checkStudentEligibility(String studentId) throws LibraryFaultException {
        System.out.println("SOAP Request: Checking legacy criteria eligibility logs for Student: " + studentId);

        if ("FAULT_TEST".equalsIgnoreCase(studentId)) {
            // Checked exception triggers JAX-WS to assemble a real XML SOAP Fault element
            // block
            throw new LibraryFaultException(
                    "CRITICAL ACCOUNT BLOCK: Student has outstanding library fine fees exceeding RM50.00. Operation terminated.");
        }

        return true;
    }
}