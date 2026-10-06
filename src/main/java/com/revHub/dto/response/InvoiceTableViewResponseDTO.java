package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceTableViewResponseDTO {
    private String customerName;
    private Long jobId;
    private String jobCardNumber;
    private Long invoiceId;
    private String invoiceNumber;
    private String createdUser;
    private LocalDateTime createdDate;
    private double grandTotal;
    private String status;
}