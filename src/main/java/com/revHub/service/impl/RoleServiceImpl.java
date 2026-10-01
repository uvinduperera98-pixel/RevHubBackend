package com.revHub.service.impl;

import com.revHub.dto.request.RoleSaveRequestDTO;
import com.revHub.dto.request.RoleSearchRequestDTO;
import com.revHub.dto.response.RoleNameResponseDTO;
import com.revHub.repository.RoleRepository;
import com.revHub.service.RoleService;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public ResponseEntity<StandardResponse> searchRole(RoleSearchRequestDTO request, Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity<StandardResponse> saveRole(RoleSaveRequestDTO roleSaveRequestDTO) {
        return null;
    }

    @Override
    public List<RoleNameResponseDTO> getAllRoleNames() {
        return roleRepository.findAllRoleNames();
    }
}
