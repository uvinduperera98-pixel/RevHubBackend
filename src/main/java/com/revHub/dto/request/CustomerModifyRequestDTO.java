package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerModifyRequestDTO {
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private Boolean active;
    private String email;
    private String drivingLicenseNumber;
}

