package com.smartcampus.booking.services;

import org.springframework.stereotype.Service;
import jakarta.xml.soap.*;
import org.w3c.dom.NodeList;

import com.smartcampus.booking.models.Booking;
import com.smartcampus.booking.repositories.BookingRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    // Use the Docker container service name and the port defined
    private static final String SOAP_ENDPOINT = "http://library-soap-svc:8888/ws/library";

    // concurrency control - prevents multiple concurrent requests from booking the
    // same room/book simultaneously
    private final ConcurrentHashMap<String, ReentrantLock> resourceLocks = new ConcurrentHashMap<>();

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // ensures all requests targeting the same resource share the same lock
    private ReentrantLock lockFor(String key) {
        return resourceLocks.computeIfAbsent(key, k -> new ReentrantLock());
    public List<Booking> getAllBookingsList() {
        // Calls your JPA repository method to grab all seeded rows
        return bookingRepository.findAll(); 
    }

    // Reserve discussion room
    public Map<String, String> reserveLegacyRoom(String roomId, String studentId, String bookingDate) {
        // lock specific room and time slot before checking availability
        String lockKey = "ROOM:" + roomId + ":" + bookingDate;
        ReentrantLock lock = lockFor(lockKey);
        lock.lock();
        try {
            LocalDateTime slot = LocalDateTime.parse(bookingDate);
            boolean alreadyBooked = bookingRepository
                    .existsByResourceIdAndStartTimeAndStatus(roomId, slot, "ACTIVE");

            if (alreadyBooked) {
                Map<String, String> conflict = new HashMap<>();
                conflict.put("status", "CONFLICT");
                conflict.put("message", "Room " + roomId + " is already booked for " + bookingDate);
                return conflict;
            }

            return callLegacySoapSystem("reserveDiscussionRoom", Map.of(
                    "roomId", roomId,
                    "studentId", studentId,
                    "bookingDate", bookingDate), roomId, studentId);

        } catch (

        Exception e) {
            // catches bad bookingDate format so it doesn't crash the request with an
            // unhandled exception
            Map<String, String> result = new HashMap<>();
            result.put("status", "ERROR");
            result.put("message", "Invalid bookingDate format, expected ISO-8601 (e.g. 2026-06-25T10:00:00).");
            return result;
        } finally {
            lock.unlock();
        }
    }

    // Loan book operation
    public Map<String, String> loanLegacyBook(String bookId, String studentId) {
        // lock specific book before checking loan status
        String lockKey = "BOOK:" + bookId;
        ReentrantLock lock = lockFor(lockKey);
        lock.lock();
        try {
            // check if this book is already on active loan before calling the legacy system
            boolean alreadyOnLoan = bookingRepository
                    .existsByResourceIdAndStatus(bookId, "ACTIVE");

            if (alreadyOnLoan) {
                Map<String, String> conflict = new HashMap<>();
                conflict.put("status", "CONFLICT");
                conflict.put("message", "Book " + bookId + " is currently on loan.");
                return conflict;
            }

            return callLegacySoapSystem("loanBook", Map.of(
                    "bookId", bookId,
                    "studentId", studentId), bookId, studentId);
        } finally {
            lock.unlock();
        }
    }

    // CENTRALIZED REUSABLE SOAP HANDLING ENGINE 
 private Map<String, String> callLegacySoapSystem(String operation, Map<String, String> arguments, String resourceId,
            String studentId) {

        // 1. Determine time and perform local Database Conflict Check FIRST
        LocalDateTime slotTime = LocalDateTime.now();
        
        if (arguments.containsKey("bookingDate")) {
            // Room Reservation Check: Is it booked at this exact time?
            slotTime = LocalDateTime.parse(arguments.get("bookingDate"));
            if (bookingRepository.existsByResourceIdAndStartTimeAndStatus(resourceId, slotTime, "ACTIVE")) {
                return Map.of("status", "CONFLICT", "message", "Discussion Room " + resourceId + " is already reserved for this time slot.");
            }
        } else {
            // Book Loan Check: Is this book currently out right now?
            if (bookingRepository.existsByResourceIdAndStatus(resourceId, "ACTIVE")) {
                return Map.of("status", "CONFLICT", "message", "The book " + resourceId + " has already been loaned out.");
            }
        }

        Map<String, String> result = new HashMap<>();

        // 2. Instantiate local audit log entity
        Booking localBooking = new Booking();
        localBooking.setStudentId(studentId);
        localBooking.setResourceId(resourceId);
        localBooking.setStartTime(slotTime); // 🔥 FIX: Saves actual booking time, not current server time

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();

            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            SOAPBody soapBody = envelope.getBody();

            // Match target name and contract namespaces exactly
            Name operationName = envelope.createName(operation, "ns", "http://library_system.com/");
            SOAPBodyElement methodElement = soapBody.addBodyElement(operationName);

            // Dynamically attach parameters to the XML packet payload
            for (Map.Entry<String, String> arg : arguments.entrySet()) {
                methodElement.addChildElement(arg.getKey()).addTextNode(arg.getValue());
            }

            soapMessage.saveChanges();

            // Establish network connection and dispatch
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapResponse = soapConnection.call(soapMessage, SOAP_ENDPOINT);
            soapConnection.close();

            SOAPBody responseBody = soapResponse.getSOAPBody();

            // Intercept target legacy system SOAP Fault exceptions
            if (responseBody.hasFault()) {
                SOAPFault fault = responseBody.getFault();
                result.put("status", "FAULT");
                result.put("message", "Legacy System Fault: " + fault.getFaultString());

                localBooking.setStatus("FAILED");
                bookingRepository.save(localBooking);
                return result;
            }

            // Parse output string out of payload document structure
            NodeList returnNodes = responseBody.getElementsByTagName("return");

            if (returnNodes.getLength() > 0) {
                String rawResponseText = returnNodes.item(0).getTextContent();
                String[] parts = rawResponseText.split("\\|");

                String status = parts[0];
                result.put("status", status);
                result.put("message", parts.length > 1 ? parts[1] : rawResponseText);

                localBooking.setStatus("SUCCESS".equalsIgnoreCase(status) ? "ACTIVE" : "FAILED");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Empty XML payload response from legacy library backend.");
                localBooking.setStatus("FAILED");
            }

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Failed to communicate with legacy SOAP system: " + e.getMessage());
            localBooking.setStatus("FAILED");
        }

        // Commit transaction tracking state data to local database before returning
        bookingRepository.save(localBooking);
        return result;
    }
}