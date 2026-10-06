package com.revHub.integration.vehiclesdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ModelDto(
        String name,
        String slug,
        String kind
) {}
