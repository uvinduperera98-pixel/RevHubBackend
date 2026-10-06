package com.revHub.dto.response;

public interface JobCardStatusResponseProjection {
    Long getPendingCount();
    Long getInProgressCount();
    Long getRejectedCount();
    Long getCompletedCount();
}
