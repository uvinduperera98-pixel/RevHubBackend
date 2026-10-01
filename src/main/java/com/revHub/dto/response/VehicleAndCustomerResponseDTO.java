package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleAndCustomerResponseDTO {
    private String vehicleRegNo;
    private String vehicleVinNo;
    private String vehicleMake;
    private String vehicleModel;
    private int vehicleYear;
    private String colour;
    private String otherSpecs;
    private String customerName;
    private String customerAddress;
    private String email;
    private String contactNumbers;
    private String drivingLicenseNumber;

}
