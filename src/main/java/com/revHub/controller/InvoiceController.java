package com.revHub.controller;

import com.revHub.dto.request.InvoiceModifyRequestDTO;
import com.revHub.dto.request.InvoiceSaveRequestDTO;
import com.revHub.dto.request.InvoiceSearchRequestDTO;
import com.revHub.dto.response.InvoiceResponseDTO;
import com.revHub.dto.response.InvoiceTableViewResponseDTO;
import com.revHub.dto.response.PdfPreviewResponseDTO;
import com.revHub.service.InvoiceService;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/save")
    public ResponseEntity<StandardResponse> createInvoice(
            @RequestBody InvoiceSaveRequestDTO requestDto) {

        Map<String, Object> responseData = invoiceService.saveNewInvoice(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new StandardResponse(
                        HttpStatus.CREATED.value(),
                        "Invoice generated and posted successfully!",
                        responseData
                ));
    }

    @GetMapping("/get-invoice-pdf-by-invoiceId/{invoiceId}")
    public ResponseEntity<StandardResponse> getInvoicePdfById(@PathVariable Long invoiceId) {

        PdfPreviewResponseDTO pdfPreviewResponseDTO = invoiceService.getInvoicePdfById(invoiceId);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Invoice PDF fetched successfully",
                        pdfPreviewResponseDTO
                )
        );
    }

    @PostMapping("/search")
    public ResponseEntity<StandardResponse> searchInvoices(
            @RequestBody InvoiceSearchRequestDTO request,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<InvoiceTableViewResponseDTO> result = invoiceService.searchInvoices(request, pageable);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Invoices retrieved successfully!",
                        result
                )
        );
    }

    @GetMapping("/get-by-id/{invoiceId}")
    public ResponseEntity<StandardResponse> getInvoiceById(@PathVariable Long invoiceId) {

        InvoiceResponseDTO response = invoiceService.getInvoiceById(invoiceId);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Invoice fetched successfully",
                        response
                )
        );
    }

    @PutMapping("/modify")
    public ResponseEntity<StandardResponse> updateInvoice(@RequestBody InvoiceModifyRequestDTO requestDto) {

        Map<String, Object> responseData = invoiceService.updateInvoice(requestDto);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Invoice updated successfully!",
                        responseData
                )
        );
    }

}
