package com.revHub.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public interface JobCardResponseDto {
    Long getJobId();
    LocalDateTime getCreatedDate();
    LocalDateTime getEstimatedCompletionTime();
    String getStatus();
    String getCustomerComplaintText();
    Double getCurrentMileage(); // Match the entity's Double type
    VehicleResponseProjection getVehicle();
    List<TechnicianResponseProjection> getTechnicians();
    List<LaborActivityTableViewResponseProjection> getLaborActivities();
}
