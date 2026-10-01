package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSaveRequestDTO {
    private String vehicleRegNo;
    private String vehicleVinNo;
    private String vehicleMake;
    private String vehicleModel;
    private int vehicleYear;
    private double vehicleMileage;
    private String colour;
    private String otherSpecs;
    private Long customerId;
}
