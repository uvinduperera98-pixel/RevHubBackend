package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDTO {
    private Long invoiceId;
    private String jobCardNumber;
    private String paymentMethod;
    private Double additionalFees;
    private String status;
    private List<InvoiceLaborActivityResponseDTO> laborActivities;
}
