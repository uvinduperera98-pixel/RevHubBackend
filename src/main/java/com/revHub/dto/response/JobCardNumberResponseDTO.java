package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobCardNumberResponseDTO {

    private long jobId;
    private String jobNumber;
}