package com.revHub.controller;

import com.revHub.dto.request.VehicleModifyRequestDTO;
import com.revHub.dto.request.VehicleSearchRequestDTO;
import com.revHub.dto.response.*;
import com.revHub.service.VehicleService;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/vehicle")
public class VehicleController {
    @Autowired
    VehicleService vehicleService;

    @GetMapping("/get-vehicle-and-customer-by-vehicle-reg-number/{vehicleRegNumber}")
    public ResponseEntity<StandardResponse> getVehicleAndCustomerByVehicleRegNumber(@PathVariable String vehicleRegNumber) {
        VehicleAndCustomerResponseDTO response = vehicleService.getVehicleAndCustomerByVehicleRegNumber(vehicleRegNumber);
        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Success", response));
    }

    @GetMapping("/get-vehicle-and-customer-by-vehicle-vin-number/{vehicleVinNumber}")
    public ResponseEntity<StandardResponse> getVehicleAndCustomerByVehicleVinNumber(@PathVariable String vehicleVinNumber) {
        VehicleAndCustomerResponseDTO response = vehicleService.getVehicleAndCustomerByVehicleVinNumber(vehicleVinNumber);
        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Success", response));
    }

    @PostMapping("/get-all-vehicles-summary")
    public ResponseEntity<StandardResponse> getAllVehicle(
            @RequestBody VehicleSearchRequestDTO request,
            @PageableDefault(page = 0, size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VehicleTableViewResponseProjection> result = vehicleService.getAllVehiclesSummaryPaginated(request, pageable);

        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Vehicle retrieved successfully!", result));
    }

    @GetMapping("/get-vehicle-by-vehicleId/{vehicleId}")
    public ResponseEntity<StandardResponse> getVehicleByVehicleId(@PathVariable Long vehicleId) {
        VehicleResponseProjection vehicle = vehicleService.getVehicleByVehicleId(vehicleId);
        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Success", vehicle));
    }

    @PutMapping("/modify")
    public ResponseEntity<StandardResponse> updateVehicle(@RequestBody VehicleModifyRequestDTO dto) {
        vehicleService.updateVehicle(dto);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Vehicle updated successfully!",
                null
        ));
    }

    @GetMapping("/get-all-vehicle-reg-nos")
    public ResponseEntity<StandardResponse> getAllVehicleRegNoList() {
        List<String> regNoList = vehicleService.getVehicleRegNoList();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                regNoList
        ));
    }

    @GetMapping("/get-all-vehicle-vin-nos")
    public ResponseEntity<StandardResponse> getAllVehicleVinNoList() {
        List<String> vinNoList = vehicleService.getVehicleVinNoList();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                vinNoList
        ));
    }

    @GetMapping("/get-all-vehicle-makes")
    public ResponseEntity<StandardResponse> getAllVehicleMakes() {

        List<VehicleMakeResponseDTO> makeList = vehicleService.getAllVehicleMakes();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                makeList
        ));
    }

    @GetMapping("/get-vehicle-models-by-make-id/{makeId}")
    public ResponseEntity<StandardResponse> getAllVehicleModelsByMake(@PathVariable Long makeId) {

        List<VehicleModelResponseDTO> modelList = vehicleService.getAllVehicleModelsByMake(makeId);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                modelList
        ));
    }
}
