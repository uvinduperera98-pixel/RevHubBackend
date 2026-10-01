package com.revHub.dto.request;

import com.revHub.entity.enums.MeasuringUnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartItemRequestDTO {
    private Long itemId;
    private String name;        // Item / Service Part catalog name selection string
    private int qty;            // Quantity requested
    private double unitPrice;   // Selling price value bound at checkout
    private MeasuringUnitType unitType;
}
