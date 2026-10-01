package com.revHub.dto.response;

public interface VehicleResponseProjection {
    Long getVehicleId();

    String getVehicleRegNo();

    String getVehicleVinNo();

    String getVehicleMake();

    String getVehicleModel();

    Integer getVehicleYear();

    String getColour();

    String getOtherSpecs();

    CustomerResponseProjection getCustomer();
}
