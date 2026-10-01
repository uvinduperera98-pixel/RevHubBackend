package com.revHub.service;

import com.revHub.dto.request.InvoiceModifyRequestDTO;
import com.revHub.dto.request.InvoiceSaveRequestDTO;
import com.revHub.dto.request.InvoiceSearchRequestDTO;
import com.revHub.dto.response.InvoiceResponseDTO;
import com.revHub.dto.response.InvoiceTableViewResponseDTO;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface InvoiceService {
    Map<String, Object> saveNewInvoice(InvoiceSaveRequestDTO dto);

    Page<InvoiceTableViewResponseDTO> searchInvoices(InvoiceSearchRequestDTO request, Pageable pageable);

    byte[] getInvoicePdfById(Long invoiceId);

    InvoiceResponseDTO getInvoiceById(Long invoiceId);

    Map<String, Object> updateInvoice(InvoiceModifyRequestDTO dto);
}
