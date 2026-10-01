package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardSaveRequestDTO {
    private Long vehicleId;
    private LocalDateTime estimatedCompletionTime;
    private String status;
    private String customerComplaintText;
    private Boolean existVehicle;
    private Boolean existCustomer;
    private Double currentMileage;
    private CustomerSaveRequestDTO customerSaveRequestDTO;
    private VehicleSaveRequestDTO vehicleSaveRequestDTO;
    private List<Long> assignedTechniciansSelected;
    private List<Long> laborActivitiesSelected;
}
