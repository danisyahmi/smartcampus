package com.smartcampus.report.models;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private long totalStudents;
    private long totalEnrollments;
    private long totalBookings;
    private long totalNotifications;
    
    // Quick breakdowns for charting/analytics
    private Map<String, Long> enrollmentStatusBreakdown;
    private Map<String, Long> bookingStatusBreakdown;
}
