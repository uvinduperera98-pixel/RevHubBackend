package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleModelResponseDTO {
    private Long id;
    private Long vehicleMakeId;
    private String name;
}
