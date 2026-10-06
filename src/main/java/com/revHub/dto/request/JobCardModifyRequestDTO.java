package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardModifyRequestDTO {
    private Long jobId;
    private List<Long> assignedTechniciansSelected;
    private List<Long> laborActivitiesSelected;
    private String customerComplaintText;
    private Double currentMileage;
}
