package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechnicianSaveRequestDTO {
    private String technicianName;
    private String technicianContact;
    private String speciality;
}
