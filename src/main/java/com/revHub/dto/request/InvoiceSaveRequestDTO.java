package com.revHub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceSaveRequestDTO {
    private String jobCardSearch;
    private String paymentMethod;
    private double additionalFees;
    private double discountAmount;
    private double grandTotal;
    private String status;
    private String additionalNotes;
    private List<LaborActivityRequestDTO> laborActivities;
}
