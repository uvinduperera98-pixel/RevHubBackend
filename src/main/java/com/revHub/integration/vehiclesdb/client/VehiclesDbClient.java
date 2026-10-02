package com.revHub.integration.vehiclesdb.client;

import com.revHub.integration.vehiclesdb.dto.VehiclesDbResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class VehiclesDbClient {

    private final RestClient restClient;

    private static final String VEHICLES_DB_URL =
            "https://cdn.jsdelivr.net/gh/vehiclesdb/vehiclesdb@latest/dist/vehicles.json";

    public VehiclesDbResponse download() {

        return restClient
                .get()
                .uri(VEHICLES_DB_URL)
                .retrieve()
                .body(VehiclesDbResponse.class);
    }
}