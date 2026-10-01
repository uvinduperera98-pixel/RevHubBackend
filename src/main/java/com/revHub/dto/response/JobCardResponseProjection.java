package com.revHub.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public interface JobCardResponseProjection {

    // --- Job Card Core ---
    Long getJobId();
    LocalDateTime getCreatedDate();
    LocalDateTime getEstimatedCompletionTime();
    String getStatus();
    String getCustomerComplaintText();
    String getCurrentMileage();

    // --- Vehicle Nested Fields ---
    VehicleSummary getVehicle();

    // --- Collections ---
    List<TechnicianSummary> getTechnicians();
    List<LaborActivitySummary> getLaborActivities();

    // --- Nested Structures ---
    interface VehicleSummary {
        Long getVehicleId();
        String getVehicleRegNo();
        String getVehicleMake();
        String getVehicleModel();
        int getVehicleYear();
        String getColour();
        String getOtherSpecs();
        CustomerSummary getCustomer(); // Traverses deeply into Customer
    }

    interface CustomerSummary {
        Long getCustomerId();
        String getCustomerName();
        String getCustomerAddress();
        String getEmail();
        String getContactNumber();
        String getDrivingLicenseNumber();
        Boolean isActive();
    }

    interface TechnicianSummary {
        Long getTechnicianId();
        String getTechnicianName();
        String getTechnicianContact();
    }

    interface LaborActivitySummary {
        Long getLaborActivityId();
        String getActivityName();
        double getHourlyRate();
        double getFlatRateCharge();
        double getEstimatedDurationHours();
        Boolean isActive();
    }
}