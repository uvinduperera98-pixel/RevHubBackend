package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaborActivityRequestDTO {
    private Long id;
    private String name;           // Labor activity ID/Selection name
    private double laborFee;       // Flat cost fee for this labor action

    // Nested child parts array assigned under this task matrix row wrapper
    private List<PartItemRequestDTO> parts;
}