package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerContactNumberEmailIdsResponseDto {
    private Long customerId;
    private String contactNumber;
    private String email;
}
