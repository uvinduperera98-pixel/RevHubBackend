package com.revHub.repository;

import com.revHub.dto.response.VehicleResponseProjection;
import com.revHub.dto.response.VehicleTableViewResponseProjection;
import com.revHub.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findVehiclesByVehicleRegNo(String vehicleRegNumber);

    @Query("SELECT v FROM Vehicle v WHERE v.vehicleId = :vehicleId")
    Optional<VehicleResponseProjection> findVehicleByVehicleId(@Param("vehicleId") Long vehicleId);

    Vehicle findVehiclesByVehicleVinNo(String vehicleVinNumber);

    @Query("SELECT v.vehicleRegNo FROM Vehicle v WHERE v.vehicleRegNo IS NOT NULL AND TRIM(v.vehicleRegNo) <> '' AND v.vehicleRegNo <> 'N/A'")
    List<String> findAllVehicleRegNos();

    @Query("SELECT v.vehicleVinNo FROM Vehicle v WHERE v.vehicleVinNo IS NOT NULL AND TRIM(v.vehicleVinNo) <> '' AND v.vehicleVinNo <> 'N/A'")
    List<String> findAllVehicleVinNos();

    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:vehicleRegNo IS NULL OR :vehicleRegNo = '' OR v.vehicleRegNo LIKE %:vehicleRegNo%) AND " +
            "(:vehicleVinNo IS NULL OR :vehicleVinNo = '' OR v.vehicleVinNo LIKE %:vehicleVinNo%)")
    Page<VehicleTableViewResponseProjection> findAllVehiclesPaginated(
            @Param("vehicleRegNo") String vehicleRegNo,
            @Param("vehicleVinNo") String vehicleVinNo,
            Pageable pageable
    );

    @Query("SELECT COUNT(v) > 0 FROM Vehicle v WHERE " +
            "(:regNo IS NOT NULL AND :regNo != '' AND :regNo != 'N/A' AND v.vehicleRegNo = :regNo) OR " +
            "(:vinNo IS NOT NULL AND :vinNo != '' AND :vinNo != 'N/A' AND v.vehicleVinNo = :vinNo)")
    boolean existsVehicleByVehicleRegNoNoNullOrVehicleVinNoNull(
            @Param("regNo") String regNo,
            @Param("vinNo") String vinNo
    );
}
