package com.revHub.service;

import com.revHub.dto.request.RoleSaveRequestDTO;
import com.revHub.dto.request.RoleSearchRequestDTO;
import com.revHub.dto.response.RoleNameResponseDTO;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleService {
    ResponseEntity<StandardResponse> searchRole(RoleSearchRequestDTO request, Pageable pageable);

    ResponseEntity<StandardResponse> saveRole(RoleSaveRequestDTO roleSaveRequestDTO);

    List<RoleNameResponseDTO> getAllRoleNames();
}
