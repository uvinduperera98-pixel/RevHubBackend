package com.revHub.integration.vehiclesdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MakeDto(
        String name,
        String slug,
        List<String> kinds,
        List<ModelDto> models
) {}
