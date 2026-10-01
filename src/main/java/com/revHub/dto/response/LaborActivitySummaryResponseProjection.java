package com.revHub.dto.response;

public interface LaborActivitySummaryResponseProjection {
    Long getLaborActivityId();
    String getActivityName();
    double getHourlyRate();
    double getFlatRateCharge();
    double getEstimatedDurationHours();
    Boolean isActive();
}
