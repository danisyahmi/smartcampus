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

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    // Use the Docker container service name and the port defined
    private static final String SOAP_ENDPOINT = "http://library-soap-svc:8888/ws/library";

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getAllBookingsList() {
        // Calls your JPA repository method to grab all seeded rows
        return bookingRepository.findAll(); 
    }

    // Reserve discussion room
    public Map<String, String> reserveLegacyRoom(String roomId, String studentId, String bookingDate) {
        return callLegacySoapSystem("reserveDiscussionRoom", Map.of(
                "roomId", roomId,
                "studentId", studentId,
                "bookingDate", bookingDate), roomId, studentId);
    }

    // Loan book operation
    public Map<String, String> loanLegacyBook(String bookId, String studentId) {
        return callLegacySoapSystem("loanBook", Map.of(
                "bookId", bookId,
                "studentId", studentId), bookId, studentId);
    }

    // CENTRALIZED REUSABLE SOAP HANDLING ENGINE 
    private Map<String, String> callLegacySoapSystem(String operation, Map<String, String> arguments, String resourceId,
            String studentId) {
        Map<String, String> result = new HashMap<>();

        // Instantiate local audit log entity
        Booking localBooking = new Booking();
        localBooking.setStudentId(studentId);
        localBooking.setResourceId(resourceId);
        localBooking.setStartTime(LocalDateTime.now());

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