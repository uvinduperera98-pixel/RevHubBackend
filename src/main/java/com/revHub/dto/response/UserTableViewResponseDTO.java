package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTableViewResponseDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String speciality;
    private List<RoleNameResponseDTO> role;
    private Boolean active;
}
