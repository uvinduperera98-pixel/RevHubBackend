package com.revHub.integration.vehiclesdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VehiclesDbResponse(
        String version,
        Integer schema_version,
        String region,
        List<String> kinds,
        List<MakeDto> makes
) {}
