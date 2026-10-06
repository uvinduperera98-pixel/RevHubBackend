package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSaveRequestDTO {
    private String customerName;
    private String customerAddress;
    private String contactNumber;
    private Boolean active;
    private String email;
    private String drivingLicenseNumber;
}

