package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerSearchRequestDTO {
    private String contactNumber;
    private String email;
    private String activeStatus;
}
