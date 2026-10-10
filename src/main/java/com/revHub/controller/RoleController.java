package com.revHub.controller;

import com.revHub.dto.request.RoleSaveRequestDTO;
import com.revHub.dto.request.RoleSearchRequestDTO;
import com.revHub.dto.response.RoleNameResponseDTO;
import com.revHub.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<StandardResponse> saveRole(@RequestBody RoleSaveRequestDTO roleSaveRequestDTO) {
        return roleService.saveRole(roleSaveRequestDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<StandardResponse> searchRole(
            @RequestBody RoleSearchRequestDTO request,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return roleService.searchRole(request, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNISION')")
    @GetMapping("/get-all-role-names")
    public ResponseEntity<StandardResponse> getAllRoleNames() {

        List<RoleNameResponseDTO> roleNames = roleService.getAllRoleNames();

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Role names retrieved successfully",
                        roleNames
                )
        );
    }
}
