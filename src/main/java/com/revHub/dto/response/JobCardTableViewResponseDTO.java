package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardTableViewResponseDTO {
    private Long jobId;
    private String jobCardNumber;
    private String customerName;
    private String vehicleRegNumber;
    private String vehicleVinNumber;
    private String createdUser;
    private LocalDateTime createdDate;
    private String status;
    private String technicianName; // Can be a comma-separated string if handled, or primary technician
}
