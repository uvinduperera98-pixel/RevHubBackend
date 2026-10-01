package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyRequestDTO {
    // Unique identity name used during system login check screens
    private Long userId;

    // Display identity for the logged-in profile wrapper (e.g., "Admin", "John Doe")
    private String fullName;

    private String speciality;

    private Set<Long> roleIds;

    // Account status check marker flag
    private Boolean active = true;
}
