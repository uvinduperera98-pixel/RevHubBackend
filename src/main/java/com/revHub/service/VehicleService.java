package com.revHub.service;

import com.revHub.dto.request.VehicleModifyRequestDTO;
import com.revHub.dto.request.VehicleSearchRequestDTO;
import com.revHub.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {

    VehicleAndCustomerResponseDTO getVehicleAndCustomerByVehicleRegNumber(String vehicleRegNumber);

    Page<VehicleTableViewResponseProjection> getAllVehiclesSummaryPaginated(VehicleSearchRequestDTO request, Pageable pageable);

    VehicleResponseProjection getVehicleByVehicleId(Long vehicleId);

    void updateVehicle(VehicleModifyRequestDTO dto);

    VehicleAndCustomerResponseDTO getVehicleAndCustomerByVehicleVinNumber(String vehicleVinNumber);

    List<String> getVehicleRegNoList();

    List<String> getVehicleVinNoList();

    List<VehicleMakeResponseDTO> getAllVehicleMakes();

    List<VehicleModelResponseDTO> getAllVehicleModelsByMake(Long makeId);
}
