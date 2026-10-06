package com.revHub.dto.response;

import com.revHub.entity.enums.MeasuringUnitType;
import java.time.LocalDateTime;
import java.util.List;

public interface ItemTableViewResponseProjection {
    Long getItemId();
    String getItemName();
    double getBalanceQty();

    // Use correct types for Audit fields
    LocalDateTime getCreatedDate();
    LocalDateTime getLastModifiedDate();
    String getLastModifiedUser();

    double getSupplierPrice();
    double getSellingPrice();
    MeasuringUnitType getMeasuringUnitType();
    List<LaborActivityNameResponseProjection> getLaborActivities();
}
