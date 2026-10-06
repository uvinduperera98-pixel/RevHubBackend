package com.revHub.integration.vehiclesdb.repository;

import com.revHub.dto.response.VehicleModelResponseDTO;
import com.revHub.integration.vehiclesdb.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleModelRepository
        extends JpaRepository<VehicleModel, Long> {

    Optional<VehicleModel> findByMakeIdAndNormalizedName(
            Long makeId,
            String normalizedName
    );

    boolean existsByMakeIdAndNormalizedName(
            Long makeId,
            String normalizedName
    );

    @Query("""
    SELECT new com.revHub.dto.response.VehicleModelResponseDTO(
        vm.id,
        vm.make.id,
        vm.normalizedName
    )
    FROM VehicleModel vm
    WHERE vm.make.id = :makeId
    AND vm.active = true
    ORDER BY vm.normalizedName
    """)
    List<VehicleModelResponseDTO> findAllVehicleModelIdAndNameByMakeId(
            @Param("makeId") Long makeId
    );
}
