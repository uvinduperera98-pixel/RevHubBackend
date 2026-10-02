package com.revHub.integration.vehiclesdb.repository;

import com.revHub.integration.vehiclesdb.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
