package com.revHub.dto.response;

import java.time.LocalDateTime;

public interface InvoiceDashboardResponseProjection {
    String getCustomerName();
    Long getJobId();
    Long getInvoiceId();
    String getInvoiceNumber();
    LocalDateTime getInvoiceDate();
    double getGrandTotal();
    String getStatus();
}
