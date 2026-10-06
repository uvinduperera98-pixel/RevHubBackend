package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailSaveRequestDTO {
    private String labourActivityGroupName; // e.g., "Oil Change"

    // Pass item id if this line is an inventory part (null/zero otherwise)
    private Long itemId;

    // Pass labor id if this line is a mechanic service charge (null/zero otherwise)
    private Long laborActivityId;

    private double qty;
    private double unitPrice; // The selling price frozen at checkout time
    private double total;     // qty * unitPrice
}
