package com.revHub.dto.response;

public interface TechnicianTableViewResponseProjection {
    Long getTechnicianId();
    String getTechnicianName();
    String getSpeciality();
    Long getActiveJobCount();
    String getJobStatus();
}
