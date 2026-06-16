package com.library_system;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface LibraryService {

    @WebMethod
    String loanBook(
        @WebParam(name = "bookId") String bookId, 
        @WebParam(name = "studentId") String studentId
    );

    @WebMethod
    String reserveDiscussionRoom(
        @WebParam(name = "roomId") String roomId, 
        @WebParam(name = "studentId") String studentId,
        @WebParam(name = "bookingDate") String bookingDate
    );
}