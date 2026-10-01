package com.revHub.dto.response;

public interface JobCardSummaryResponseProjection {
    Long getJobId();

    String getJobCardNumber();

    String getStatus();

    String getCustomerName();
}
