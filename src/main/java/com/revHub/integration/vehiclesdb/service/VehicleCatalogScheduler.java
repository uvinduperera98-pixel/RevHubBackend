package com.revHub.integration.vehiclesdb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleCatalogScheduler {

    private final VehicleCatalogSyncService syncService;

    @Scheduled(cron = "0 0 2 * * *")
    public void sync() {

        syncService.sync();
    }
}
