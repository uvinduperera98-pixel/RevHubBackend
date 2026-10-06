package com.revHub.dto.response;

import com.revHub.entity.enums.MeasuringUnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemsResponseDTO {
    private Long itemId;
    private String itemName;
    private MeasuringUnitType unitType;
    private double sellingPrice;
}
