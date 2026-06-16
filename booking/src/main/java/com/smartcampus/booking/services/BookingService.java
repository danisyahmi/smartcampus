package com.smartcampus.booking.services;

import org.springframework.stereotype.Service;
import jakarta.xml.soap.*;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookingService {

    // Use the Docker container service name and the port defined
    private static final String SOAP_ENDPOINT = "http://library-soap-svc:8888/ws/library";

    public Map<String, String> reserveLegacyRoom(String roomId, String studentId, String bookingDate) {
        Map<String, String> result = new HashMap<>();
        try {
            // Create SOAP Message Factory
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            
            // Populate SOAP Body to match the exact legacy implementation contract
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            SOAPBody soapBody = envelope.getBody();

            // Create the operation element matching the method name in LibraryService
            // Namespace prefix 'ns' pointing to the expected service target namespace
            Name operationName = envelope.createName("reserveDiscussionRoom", "ns", "http://library_system.com/");
            SOAPBodyElement methodElement = soapBody.addBodyElement(operationName);

            // Add the ordered arguments matching the RPC contract
            methodElement.addChildElement("roomId").addTextNode(roomId);
            methodElement.addChildElement("studentId").addTextNode(studentId);
            methodElement.addChildElement("bookingDate").addTextNode(bookingDate);

            soapMessage.saveChanges();

            // Establish connection and dispatch the message over the network
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            
            SOAPMessage soapResponse = soapConnection.call(soapMessage, SOAP_ENDPOINT);
            soapConnection.close();

            // Parse the raw text response out of the XML nodes
            SOAPBody responseBody = soapResponse.getSOAPBody();
            NodeList returnNodes = responseBody.getElementsByTagName("return");

            if (returnNodes.getLength() > 0) {
                // Legacy system returns formatted string 
                String rawResponseText = returnNodes.item(0).getTextContent();
                String[] parts = rawResponseText.split("\\|");
                
                result.put("status", parts[0]);
                result.put("message", parts.length > 1 ? parts[1] : rawResponseText);
            } else {
                result.put("status", "ERROR");
                result.put("message", "Empty response from legacy library backend.");
            }

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Failed to communicate with legacy SOAP system: " + e.getMessage());
        }
        return result;
    }
}