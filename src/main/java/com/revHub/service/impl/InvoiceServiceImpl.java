package com.revHub.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.revHub.dto.request.*;
import com.revHub.dto.response.*;
import com.revHub.entity.*;
import com.revHub.entity.enums.MeasuringUnitType;
import com.revHub.exception.BadRequestException;
import com.revHub.exception.InternalServerErrorException;
import com.revHub.exception.NotFoundException;
import com.revHub.exception.PdfGenerationException;
import com.revHub.repository.*;
import com.revHub.service.InvoiceService;
import com.revHub.util.StandardResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final JobCardRepository jobCardRepository;
    private final JobCardHistoryRepository jobCardHistoryRepository;
    private final InvoiceHistoryRepository invoiceHistoryRepository;
    private final InvoiceDetailHistoryRepository invoiceDetailHistoryRepository;
    private final LaborActivityRepository laborActivityRepository;
    private final TemplateEngine templateEngine;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceDetailRepository invoiceDetailRepository,
                              JobCardRepository jobCardRepository, JobCardHistoryRepository jobCardHistoryRepository, InvoiceHistoryRepository invoiceHistoryRepository, InvoiceDetailHistoryRepository invoiceDetailHistoryRepository, LaborActivityRepository laborActivityRepository,
                              TemplateEngine templateEngine) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.jobCardRepository = jobCardRepository;
        this.jobCardHistoryRepository = jobCardHistoryRepository;
        this.invoiceHistoryRepository = invoiceHistoryRepository;
        this.invoiceDetailHistoryRepository = invoiceDetailHistoryRepository;
        this.laborActivityRepository = laborActivityRepository;
        this.templateEngine = templateEngine;
    }

    // Helper DTO class structured explicitly for Thymeleaf loops presentation layer
    public static class InvoiceGroupView {
        public String laborActivityName;
        public double laborFee;
        public double groupTotal;
        public List<PartItemRequestDTO> parts = new ArrayList<>();
    }

    @Override
    @Transactional
    public Map<String, Object> saveNewInvoice(InvoiceSaveRequestDTO dto) {

        if (dto.getJobCardSearch() == null || dto.getJobCardSearch().trim().isEmpty()) {
            throw new BadRequestException("Job Card search token value cannot be empty.");
        }

        long parsedJobId;

        try {
            parsedJobId = Long.parseLong(dto.getJobCardSearch().trim());
        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    "Invalid Job Card format. Expected a numerical ID, but received: '"
                            + dto.getJobCardSearch() + "'"
            );
        }

        JobCard jobCard = jobCardRepository.findByJobId(parsedJobId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Job Card not found with ID: " + parsedJobId
                        ));

        JobCardHistory history = new JobCardHistory();
        history.setJobId(jobCard.getJobId());
        history.setJobCardNumber(jobCard.getJobCardNumber());
        history.setVehicleId(jobCard.getVehicle().getVehicleId());
        history.setEstimatedCompletionTime(jobCard.getEstimatedCompletionTime());
        history.setStatus(jobCard.getStatus());
        history.setCustomerComplaintText(jobCard.getCustomerComplaintText());
        history.setCurrentMileage(jobCard.getCurrentMileage());
        history.setActionType("SYSTEM_AUTO_COMPLETE");

        jobCardHistoryRepository.save(history);

        Invoice invoice = new Invoice();

        jobCard.setStatus("Completed");
        invoice.setJobCard(jobCard);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setPaymentMethod(dto.getPaymentMethod());
        invoice.setAdditionalFees(dto.getAdditionalFees());
        invoice.setDiscountAmount(dto.getDiscountAmount());
        invoice.setAdditionalNotes(dto.getAdditionalNotes());
        invoice.setStatus(dto.getStatus() != null ? dto.getStatus() : "PAID");
        invoice.setInvoiceNumber(null);

        invoice = invoiceRepository.save(invoice);

        invoice.setInvoiceNumber(
                String.format("INV-%06d", invoice.getInvoiceId())
        );

        List<InvoiceDetail> allInvoiceDetails = new ArrayList<>();
        List<InvoiceGroupView> invoiceGroups = new ArrayList<>();

        double calculatedGrandTotal = dto.getAdditionalFees();

        if (dto.getLaborActivities() != null) {
            for (LaborActivityRequestDTO laborDto : dto.getLaborActivities()) {

                InvoiceGroupView group = new InvoiceGroupView();
                group.laborActivityName = laborDto.getName();
                group.laborFee = laborDto.getLaborFee();
                group.parts = laborDto.getParts() != null
                        ? laborDto.getParts()
                        : new ArrayList<>();

                double groupRunningSum = laborDto.getLaborFee();

                InvoiceDetail laborLine = new InvoiceDetail();
                laborLine.setInvoice(invoice);
                laborLine.setType("LABOR");
                laborLine.setItemId(null);
                laborLine.setLaborActivityId(laborDto.getId());
                laborLine.setDescription("Labor Charge");
                laborLine.setQty(1);
                laborLine.setUnitType(MeasuringUnitType.NUMBER);
                laborLine.setUnitPrice(laborDto.getLaborFee());
                laborLine.setTotal(laborDto.getLaborFee());

                allInvoiceDetails.add(laborLine);

                for (PartItemRequestDTO partDto : group.parts) {

                    double totalPartCost =
                            partDto.getQty() * partDto.getUnitPrice();

                    groupRunningSum += totalPartCost;

                    InvoiceDetail partLine = new InvoiceDetail();
                    partLine.setInvoice(invoice);
                    partLine.setType("PART");
                    partLine.setItemId(partDto.getItemId());
                    partLine.setLaborActivityId(laborDto.getId());
                    partLine.setDescription(partDto.getName());
                    partLine.setQty(partDto.getQty());
                    partLine.setUnitType(partDto.getUnitType());
                    partLine.setUnitPrice(partDto.getUnitPrice());
                    partLine.setTotal(totalPartCost);

                    allInvoiceDetails.add(partLine);
                }

                group.groupTotal = groupRunningSum;
                invoiceGroups.add(group);
                calculatedGrandTotal += groupRunningSum;
            }
        }

        if (!allInvoiceDetails.isEmpty()) {
            invoiceDetailRepository.saveAll(allInvoiceDetails);
        }

        invoice.setGrandTotal(calculatedGrandTotal);
        invoice.setInvoiceDetails(allInvoiceDetails);

        invoice = invoiceRepository.save(invoice);

        try {
            byte[] pdfBytes = generateInvoicePdf(invoice, invoiceGroups);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("invoiceNumber", invoice.getInvoiceNumber());
            responseData.put("invoiceId", invoice.getInvoiceId());
            responseData.put("grandTotal", invoice.getGrandTotal());
            responseData.put("pdfBytes", pdfBytes);

            return responseData;

        } catch (Exception e) {
            log.error(
                    "Failed to generate PDF for Invoice ID {}: {}",
                    invoice.getInvoiceId(),
                    e.getMessage(),
                    e
            );

            throw new PdfGenerationException(
                    "Failed to generate invoice PDF.",
                    e
            );
        }
    }

    private byte[] generateInvoicePdf(Invoice invoice, List<InvoiceGroupView> invoiceGroups) throws Exception {
        // Re-generate the updated document using Thymeleaf and HTML rendering pipeline engine
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Default fallback DTO just in case
        invoiceTempUserDto invoiceTempUserDto = new invoiceTempUserDto(null, "system", "System User");

        // 3. Extract secure details if the principal matches our custom record
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUserPrincipal(Long userId, String username, String fullName)) {
            invoiceTempUserDto = new invoiceTempUserDto(userId, username, fullName);
        }
        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("invoiceGroups", invoiceGroups);
        context.setVariable("user", invoiceTempUserDto);

        // --- Add Base64 Image Logic Here ---
        try {
            ClassPathResource imgFile = new ClassPathResource("static/images/revHub.jpeg");
            byte[] bytes = imgFile.getInputStream().readAllBytes();
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
            context.setVariable("base64Image", base64Image);
        } catch (Exception e) {
            // Fallback gracefully if the image file cannot be found
            context.setVariable("base64Image", "");
        }
        // -----------------------------------

        // 1. Process template to a string
        String processedHtml = templateEngine.process("invoice-template", context);

        // 2. Programmatic Safety Check: Escape any accidental raw '&' coming from the database!
        // This replaces any '&' that isn't already part of an HTML entity sequence (like &amp; or &lt;)
        processedHtml = processedHtml.replaceAll("&(?![A-Za-z0-9#]+;)", "&amp;");

        // 3. Render out the PDF bytes safely
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(processedHtml, "/");
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        }
    }

    private List<InvoiceGroupView> mapToInvoiceGroups(Invoice invoice) {

        Map<Long, InvoiceGroupView> groupMap = new HashMap<>();

        for (InvoiceDetail detail : invoice.getInvoiceDetails()) {

            Long laborId = detail.getLaborActivityId();

            groupMap.putIfAbsent(laborId, new InvoiceGroupView());

            InvoiceGroupView group = groupMap.get(laborId);

            if ("LABOR".equals(detail.getType())) {

                group.laborActivityName =
                        fetchLaborActivityName(detail.getLaborActivityId());

                group.laborFee = detail.getUnitPrice();
                group.groupTotal = detail.getTotal();

            } else if ("PART".equals(detail.getType())) {

                PartItemRequestDTO part = new PartItemRequestDTO();

                part.setItemId(detail.getItemId());
                part.setName(detail.getDescription());
                part.setQty(detail.getQty());
                part.setUnitPrice(detail.getUnitPrice());
                part.setUnitType(detail.getUnitType());

                group.parts.add(part);
                group.groupTotal += detail.getTotal();
            }
        }

        return new ArrayList<>(groupMap.values());
    }

    private String fetchLaborActivityName(Long laborActivityId) {
        if (laborActivityId == null) {
            return "Labor Charge";
        }
        String name = laborActivityRepository.findLaborActivityNameByLaborId(laborActivityId);
        return name != null ? name : "Labor Charge";
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceTableViewResponseDTO> searchInvoices(InvoiceSearchRequestDTO request, Pageable pageable) {

        if (pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equals("string"))) {

            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdDate").descending()
            );
        }

        LocalDateTime dateFromTime = request.getDateFrom() != null
                ? request.getDateFrom().atStartOfDay()
                : null;

        LocalDateTime dateToTime = request.getDateTo() != null
                ? request.getDateTo().atTime(23, 59, 59)
                : null;

        return invoiceRepository.searchInvoiceSummaries(
                request.getSearch(),
                request.getPaymentStatus(),
                dateFromTime,
                dateToTime,
                pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getInvoicePdfById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findInvoicesByInvoiceId(invoiceId)
                .orElseThrow(() ->
                        new NotFoundException("Invoice not found with ID: " + invoiceId));

        try {
            List<InvoiceGroupView> invoiceGroups = mapToInvoiceGroups(invoice);

            return generateInvoicePdf(invoice, invoiceGroups);

        } catch (Exception e) {
            log.error(
                    "Failed to generate PDF for Invoice ID {}: {}",
                    invoiceId,
                    e.getMessage(),
                    e
            );

            throw new PdfGenerationException(
                    "Failed to generate Invoice PDF.",
                    e
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new NotFoundException("Invoice not found with ID: " + invoiceId));

        InvoiceResponseDTO dto = new InvoiceResponseDTO();

        dto.setInvoiceId(invoice.getInvoiceId());

        if (invoice.getJobCard() != null) {
            dto.setJobCardNumber(invoice.getJobCard().getJobCardNumber());
        } else {
            dto.setJobCardNumber(invoice.getInvoiceNumber());
        }

        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setAdditionalFees(invoice.getAdditionalFees());
        dto.setStatus(invoice.getStatus());

        Map<Long, List<InvoiceDetail>> groupedDetails =
                invoice.getInvoiceDetails().stream()
                        .collect(Collectors.groupingBy(
                                detail -> detail.getLaborActivityId() != null
                                        ? detail.getLaborActivityId()
                                        : 0L
                        ));

        List<InvoiceLaborActivityResponseDTO> laborDtos = new ArrayList<>();

        for (Map.Entry<Long, List<InvoiceDetail>> entry : groupedDetails.entrySet()) {

            List<InvoiceDetail> details = entry.getValue();

            InvoiceDetail laborDetail = details.stream()
                    .filter(detail -> "LABOR".equalsIgnoreCase(detail.getType()))
                    .findFirst()
                    .orElse(null);

            InvoiceLaborActivityResponseDTO laborDto =
                    new InvoiceLaborActivityResponseDTO();

            if (laborDetail != null) {
                laborDto.setLaborActivityId(laborDetail.getLaborActivityId());
                laborDto.setActivityName(laborDetail.getDescription());
                laborDto.setLaborFee(laborDetail.getUnitPrice());
                laborDto.setIsAutoFetched(true);
            }

            List<InvoiceItemResponseDTO> partDtos = details.stream()
                    .filter(detail -> "PART".equalsIgnoreCase(detail.getType()))
                    .map(part -> {
                        InvoiceItemResponseDTO partDto = new InvoiceItemResponseDTO();

                        partDto.setItemId(part.getItemId());
                        partDto.setItemName(part.getDescription());
                        partDto.setQty(part.getQty());
                        partDto.setUnitType(part.getUnitType());
                        partDto.setUnitPrice(part.getUnitPrice());

                        return partDto;
                    })
                    .collect(Collectors.toList());

            laborDto.setParts(partDtos);
            laborDtos.add(laborDto);
        }

        dto.setLaborActivities(laborDtos);

        return dto;
    }

    @Override
    @Transactional
    public Map<String, Object> updateInvoice(InvoiceModifyRequestDTO dto) {

        if (dto.getInvoiceId() == null) {
            throw new BadRequestException(
                    "Invoice ID cannot be null for an update operation."
            );
        }

        Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                .orElseThrow(() ->
                        new NotFoundException(
                                "Invoice record not found with ID: " + dto.getInvoiceId()
                        ));

        // Save previous parent invoice state to history
        InvoiceHistory invoiceHistory = new InvoiceHistory();
        invoiceHistory.setInvoiceId(invoice.getInvoiceId());
        invoiceHistory.setJobId(invoice.getJobCard().getJobId());
        invoiceHistory.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceHistory.setInvoiceDate(invoice.getInvoiceDate());
        invoiceHistory.setPaymentMethod(invoice.getPaymentMethod());
        invoiceHistory.setAdditionalFees(invoice.getAdditionalFees());
        invoiceHistory.setDiscountAmount(invoice.getDiscountAmount());
        invoiceHistory.setGrandTotal(invoice.getGrandTotal());
        invoiceHistory.setStatus(invoice.getStatus());
        invoiceHistory.setActionType("UPDATE");

        invoiceHistoryRepository.save(invoiceHistory);

        // Save previous invoice details to history
        if (invoice.getInvoiceDetails() != null &&
                !invoice.getInvoiceDetails().isEmpty()) {

            List<InvoiceDetailHistory> detailHistories = new ArrayList<>();

            for (InvoiceDetail detail : invoice.getInvoiceDetails()) {

                InvoiceDetailHistory detailHistory = new InvoiceDetailHistory();
                detailHistory.setInvoiceDetailId(detail.getInvoiceDetailId());
                detailHistory.setInvoiceId(invoice.getInvoiceId());
                detailHistory.setType(detail.getType());
                detailHistory.setItemId(detail.getItemId());
                detailHistory.setLaborActivityId(detail.getLaborActivityId());
                detailHistory.setDescription(detail.getDescription());
                detailHistory.setQty(detail.getQty());
                detailHistory.setUnitPrice(detail.getUnitPrice());
                detailHistory.setTotal(detail.getTotal());
                detailHistory.setActionType("UPDATE");

                detailHistories.add(detailHistory);
            }

            invoiceDetailHistoryRepository.saveAll(detailHistories);
        }

        // Update invoice details
        invoice.setPaymentMethod(dto.getPaymentMethod());
        invoice.setAdditionalFees(dto.getAdditionalFees());
        invoice.setDiscountAmount(dto.getDiscountAmount());
        invoice.setStatus(
                dto.getStatus() != null
                        ? dto.getStatus()
                        : invoice.getStatus()
        );

        // Remove existing invoice details
        if (invoice.getInvoiceDetails() != null &&
                !invoice.getInvoiceDetails().isEmpty()) {

            invoiceDetailRepository.deleteAll(invoice.getInvoiceDetails());
            invoice.getInvoiceDetails().clear();
        }

        List<InvoiceDetail> allInvoiceDetails = new ArrayList<>();
        List<InvoiceGroupView> invoiceGroups = new ArrayList<>();

        double calculatedGrandTotal = dto.getAdditionalFees();

        if (dto.getLaborActivities() != null) {

            for (LaborActivityRequestDTO laborDto : dto.getLaborActivities()) {

                InvoiceGroupView group = new InvoiceGroupView();
                group.laborActivityName = laborDto.getName();
                group.laborFee = laborDto.getLaborFee();
                group.parts = laborDto.getParts() != null
                        ? laborDto.getParts()
                        : new ArrayList<>();

                double groupRunningSum = laborDto.getLaborFee();

                // Labor line
                InvoiceDetail laborLine = new InvoiceDetail();
                laborLine.setInvoice(invoice);
                laborLine.setType("LABOR");
                laborLine.setItemId(null);
                laborLine.setLaborActivityId(laborDto.getId());
                laborLine.setDescription("Labor Charge");
                laborLine.setQty(1);
                laborLine.setUnitType(MeasuringUnitType.NUMBER);
                laborLine.setUnitPrice(laborDto.getLaborFee());
                laborLine.setTotal(laborDto.getLaborFee());

                allInvoiceDetails.add(laborLine);

                // Part lines
                for (PartItemRequestDTO partDto : group.parts) {

                    double totalPartCost =
                            partDto.getQty() * partDto.getUnitPrice();

                    groupRunningSum += totalPartCost;

                    InvoiceDetail partLine = new InvoiceDetail();
                    partLine.setInvoice(invoice);
                    partLine.setType("PART");
                    partLine.setItemId(partDto.getItemId());
                    partLine.setLaborActivityId(laborDto.getId());
                    partLine.setDescription(partDto.getName());
                    partLine.setQty(partDto.getQty());
                    partLine.setUnitType(partDto.getUnitType());
                    partLine.setUnitPrice(partDto.getUnitPrice());
                    partLine.setTotal(totalPartCost);

                    allInvoiceDetails.add(partLine);
                }

                group.groupTotal = groupRunningSum;
                invoiceGroups.add(group);

                calculatedGrandTotal += groupRunningSum;
            }
        }

        // Apply discount
        if (invoice.getDiscountAmount() != null) {
            calculatedGrandTotal -= invoice.getDiscountAmount();
        }

        if (!allInvoiceDetails.isEmpty()) {
            invoiceDetailRepository.saveAll(allInvoiceDetails);
        }

        invoice.setGrandTotal(calculatedGrandTotal);
        invoice.setInvoiceDetails(allInvoiceDetails);

        invoice = invoiceRepository.save(invoice);

        try {

            byte[] pdfBytes = generateInvoicePdf(invoice, invoiceGroups);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("invoiceNumber", invoice.getInvoiceNumber());
            responseData.put("invoiceId", invoice.getInvoiceId());
            responseData.put("grandTotal", invoice.getGrandTotal());
            responseData.put("pdfBytes", pdfBytes);

            return responseData;

        } catch (Exception e) {

            log.error(
                    "Failed to generate PDF for Invoice ID {}: {}",
                    invoice.getInvoiceId(),
                    e.getMessage(),
                    e
            );

            throw new PdfGenerationException(
                    "Failed to generate Invoice PDF.",
                    e
            );
        }
    }

}
