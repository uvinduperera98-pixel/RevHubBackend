package com.revHub.dto.response;

public interface CustomerTableViewProjection {
    Long getCustomerId();

    String getCustomerName();

    String getContactNumber();

    String getEmail();

    Long getTotalJobs(); // Use Long for count results
}
