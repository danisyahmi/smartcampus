package com.library_system;

import jakarta.xml.ws.Endpoint;

public class LibraryServer {
    public static void startServer() {
        String url = "http://0.0.0.0:8888/ws/library";
        System.out.println("=================================================");
        System.out.println("Initializing Legacy Library SOAP Server...");
        System.out.println("Target Endpoint URI: " + url);
        
        // Publish the service implementation
        Endpoint.publish(url, new LibraryServiceImpl());
        
        System.out.println("Legacy System Active! Access WSDL at: " + url + "?wsdl");
        System.out.println("=================================================");
    }
}