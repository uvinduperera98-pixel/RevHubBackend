package com.revHub.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceSearchRequestDTO {
    private String search;
    private String paymentStatus;
    private LocalDate  dateFrom;
    private LocalDate dateTo;
}
