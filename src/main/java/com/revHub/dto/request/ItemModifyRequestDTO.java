package com.revHub.dto.request;

import com.revHub.entity.enums.MeasuringUnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemModifyRequestDTO {
    private Long itemId;
    private String itemName;
    private double balanceQty;
    private double supplierPrice;
    private double sellingPrice;
    private MeasuringUnitType measuringUnitType;
    private List<Long> laborActivitiesSelected;

}
