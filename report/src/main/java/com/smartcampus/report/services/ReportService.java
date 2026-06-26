package com.smartcampus.report.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.smartcampus.report.models.Report;

@Service("ReportService")
public class ReportService {
    private final RestTemplate restTemplate = new RestTemplate();

    // Internal Docker network paths pointing directly to sibling containers
    private static final String STUDENT_SERVICE_URL = "http://student-svc:8081/api/students/";
    private static final String ENROLLMENT_SERVICE_URL = "http://enrollment-svc:8082/api/enrollments/";
    private static final String BOOKING_SERVICE_URL = "http://booking-api-svc:8084/api/bookings";
    private static final String NOTIFICATION_SERVICE_URL = "http://notification-svc:8083/api/notifications";

    @SuppressWarnings("unchecked")
    public Report generateDashboardMetrics() {
        Report report = new Report();

        try {
            // Fetch Students
            List<Map<String, Object>> students = restTemplate.getForObject(STUDENT_SERVICE_URL, List.class);
            report.setTotalStudents(students != null ? students.size() : 0);

            // Fetch Enrollments and calculate breakdown
            List<Map<String, Object>> enrollments = restTemplate.getForObject(ENROLLMENT_SERVICE_URL, List.class);
            if (enrollments != null) {
                report.setTotalEnrollments(enrollments.size());
                Map<String, Long> breakdown = enrollments.stream()
                        .filter(e -> e.get("status") != null)
                        .collect(Collectors.groupingBy(e -> e.get("status").toString(), Collectors.counting()));
                report.setEnrollmentStatusBreakdown(breakdown);
            }

            // Fetch Bookings and calculate breakdown
            List<Map<String, Object>> bookings = restTemplate.getForObject(BOOKING_SERVICE_URL, List.class);
            if (bookings != null) {
                report.setTotalBookings(bookings.size());
                Map<String, Long> breakdown = bookings.stream()
                        .filter(b -> b.get("status") != null)
                        .collect(Collectors.groupingBy(b -> b.get("status").toString(), Collectors.counting()));
                report.setBookingStatusBreakdown(breakdown);
            }

            // Fetch Notifications
            try {
                List<Map<String, Object>> notifications = restTemplate.getForObject(NOTIFICATION_SERVICE_URL,
                        List.class);
                report.setTotalNotifications(notifications != null ? notifications.size() : 0);
            } catch (Exception e) {
                System.err.println("Notification Service fetch failed: " + e.getMessage());
                report.setTotalNotifications(0);
            }

        } catch (Exception e) {
            // Fallback strategy if a microservice container is booting up or down
            System.err.println("Aggregation failed due to microservice unreachable: " + e.getMessage());
        }

        return report;
    }
}
