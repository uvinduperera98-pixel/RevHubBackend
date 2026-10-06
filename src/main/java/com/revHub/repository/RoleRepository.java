package com.revHub.repository;

import com.revHub.dto.response.RoleNameResponseDTO;
import com.revHub.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT NEW com.revHub.dto.response.RoleNameResponseDTO(r.roleId, r.roleName) FROM Role r")
    List<RoleNameResponseDTO> findAllRoleNames();
}
