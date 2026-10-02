package com.revHub.integration.vehiclesdb.controller;

import com.revHub.integration.vehiclesdb.service.VehicleCatalogSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle-catalog")
@RequiredArgsConstructor
public class VehicleCatalogController {

    private final VehicleCatalogSyncService syncService;

    @PostMapping("/sync")
    public ResponseEntity<String> sync() {

        syncService.sync();

        return ResponseEntity.ok("Vehicle catalog sync completed");
    }
}
