package com.revHub.integration.vehiclesdb.repository;

import com.revHub.integration.vehiclesdb.entity.VehicleMake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleMakeRepository
        extends JpaRepository<VehicleMake, Long> {

    Optional<VehicleMake> findByNormalizedName(
            String normalizedName
    );
}
