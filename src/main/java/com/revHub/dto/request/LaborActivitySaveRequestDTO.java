package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaborActivitySaveRequestDTO {
    // Name of the mechanic work assignment (e.g., "Brake Pad Fitting", "Engine Scan Diagnostic")
    private String activityName;

    // Standard baseline hourly fee structure if calculated by clock times
    private double hourlyRate = 0.0;

    // Fixed price tag used when billing as a standard job flat-rate item
    private double flatRateCharge = 0.0;

    // Time estimate to help dispatchers allocate shop floor garage bays
    private double estimatedDurationHours = 0.0;

    // Status toggle flag for active selection availability on screens
    private Boolean active = true;
}
