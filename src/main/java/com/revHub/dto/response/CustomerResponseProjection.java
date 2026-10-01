package com.revHub.dto.response;

public interface CustomerResponseProjection {
    Long getCustomerId();

    String getCustomerName();

    String getContactNumber();

    String getEmail();

    String getDrivingLicenseNumber();

    Boolean getActive();

    String getCustomerAddress();
}
