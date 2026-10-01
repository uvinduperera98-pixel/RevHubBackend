package com.revHub.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCardSearchRequestDTO {
    private String search;
    private String vehicleRegNo;
    private String vehicleVinNo;
    private Long technicianId;
    private String status;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
