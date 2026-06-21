package com.library_system;

import jakarta.xml.ws.WebFault;

/**
 * The @WebFault annotation tells JAX-WS to dynamically map this exception
 * into a standard XML <soap:Fault> block when triggered.
 */
@WebFault(name = "LibraryFault", targetNamespace = "http://library_system.com/")
public class LibraryFaultException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public LibraryFaultException(String message) {
        super(message);
    }
    
    public LibraryFaultException(String message, Throwable cause) {
        super(message, cause);
    }
}