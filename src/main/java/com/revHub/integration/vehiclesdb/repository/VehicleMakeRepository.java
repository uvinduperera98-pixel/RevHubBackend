package com.revHub.integration.vehiclesdb.repository;

import com.revHub.dto.response.VehicleMakeResponseDTO;
import com.revHub.integration.vehiclesdb.entity.VehicleMake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleMakeRepository
        extends JpaRepository<VehicleMake, Long> {

    Optional<VehicleMake> findByNormalizedName(
            String normalizedName
    );

    @Query("""
    SELECT new com.revHub.dto.response.VehicleMakeResponseDTO(
        vm.id,
        vm.normalizedName
    )
    FROM VehicleMake vm
    WHERE vm.active = true
    ORDER BY vm.normalizedName
    """)
    List<VehicleMakeResponseDTO> findAllVehicleMakeIdAndName();
}
