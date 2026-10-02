package com.revHub.integration.vehiclesdb.service;

import java.util.Locale;

public final class VehicleNameNormalizer {

    private VehicleNameNormalizer() {}

    public static String normalize(String value) {

        if (value == null) {
            return null;
        }

        return value
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }
}
