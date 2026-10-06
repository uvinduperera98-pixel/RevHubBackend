package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechnicianModifyRequestDTO {
    private Long technicianId;
    private String technicianName;
    private String technicianContact;
    private String speciality;
}
