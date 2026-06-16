package com.smartcampus.booking;

import com.smartcampus.booking.services.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Test
    public void testCreateRoomBooking_Success() throws Exception {
        // 1. Prepare mock data matching the SOAP legacy response transformation
        Map<String, String> mockSoapResult = new HashMap<>();
        mockSoapResult.put("status", "SUCCESS");
        mockSoapResult.put("message", "Room ROOM-101 reserved successfully. Receipt: REC-TEST123");

        // 2. Instruct Mockito to intercept the service layer call
        Mockito.when(bookingService.reserveLegacyRoom("ROOM-101", "B032110001", "2026-06-20"))
               .thenReturn(mockSoapResult);

        // 3. Simulate the user's incoming REST JSON request payload
        String jsonPayload = """
            {
                "roomId": "ROOM-101",
                "studentId": "B032110001",
                "bookingDate": "2026-06-20"
            }
            """;

        // 4. Perform the mock request and assert the REST output values match up
        mockMvc.perform(post("/api/bookings/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Room ROOM-101 reserved successfully. Receipt: REC-TEST123"));
    }
}