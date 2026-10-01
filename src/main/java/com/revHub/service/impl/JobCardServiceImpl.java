package com.revHub.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.revHub.dto.request.JobCardModifyRequestDTO;
import com.revHub.dto.request.JobCardSaveRequestDTO;
import com.revHub.dto.request.JobCardSearchRequestDTO;
import com.revHub.dto.response.*;
import com.revHub.entity.*;
import com.revHub.exception.*;
import com.revHub.repository.*;
import com.revHub.service.JobCardService;
import com.revHub.util.StandardResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JobCardServiceImpl implements JobCardService {

    private final JobCardRepository jobCardRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final LaborActivityRepository laborActivityRepository;
    private final TechnicianRepository technicianRepository;
    private final JobCardHistoryRepository jobCardHistoryRepository;

    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;

    public JobCardServiceImpl(JobCardRepository jobCardRepository, VehicleRepository vehicleRepository, CustomerRepository customerRepository, LaborActivityRepository laborActivityRepository, TechnicianRepository technicianRepository, JobCardHistoryRepository jobCardHistoryRepository, ModelMapper modelMapper, TemplateEngine templateEngine) {
        this.jobCardRepository = jobCardRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.laborActivityRepository = laborActivityRepository;
        this.technicianRepository = technicianRepository;
        this.jobCardHistoryRepository = jobCardHistoryRepository;
        this.modelMapper = modelMapper;
        this.templateEngine = templateEngine;
    }

    @Override
    @Transactional
    public byte[] saveJobCard(JobCardSaveRequestDTO dto) {
        Vehicle vehicle;
        Customer customer;

        if (!dto.getExistVehicle()) {
            if (dto.getCustomerSaveRequestDTO() == null || dto.getVehicleSaveRequestDTO() == null) {
                throw new BadRequestException("Customer and vehicle details are required for new entries.");
            }

            if (customerRepository.existsCustomerByEmailOrContactNumber(
                    dto.getCustomerSaveRequestDTO().getEmail(),
                    dto.getCustomerSaveRequestDTO().getContactNumber())) {
                throw new DuplicateException("A customer with this email or contact number already exists.");
            }

            if (vehicleRepository.existsVehicleByVehicleRegNoNoNullOrVehicleVinNoNull(
                    dto.getVehicleSaveRequestDTO().getVehicleRegNo(),
                    dto.getVehicleSaveRequestDTO().getVehicleVinNo())) {
                throw new DuplicateException("A vehicle with this vehicle no or vehicle vin number already exists.");
            }

            customer = modelMapper.map(dto.getCustomerSaveRequestDTO(), Customer.class);
            customer.setActive(true);
            customer = customerRepository.save(customer);

            vehicle = modelMapper.map(dto.getVehicleSaveRequestDTO(), Vehicle.class);
            vehicle.setCustomer(customer);
            vehicle = vehicleRepository.save(vehicle);

        } else {
            if (dto.getVehicleSaveRequestDTO() == null
                    || dto.getVehicleSaveRequestDTO().getVehicleRegNo() == null
                    || dto.getVehicleSaveRequestDTO().getVehicleRegNo().isBlank()) {
                throw new BadRequestException("Vehicle registration number must be provided for existing vehicles.");
            }

            String vehicleRegNo = dto.getVehicleSaveRequestDTO().getVehicleRegNo();
            vehicle = vehicleRepository.findVehiclesByVehicleRegNo(vehicleRegNo);

            if (vehicle == null) {
                throw new NotFoundException("Vehicle not found with Registration Number: " + vehicleRegNo);
            }
        }

        JobCard jobCard = modelMapper.map(dto, JobCard.class);
        jobCard.setVehicle(vehicle);
        jobCard.setStatus("Pending");
        jobCard.setCurrentMileage(dto.getCurrentMileage());

        if (dto.getLaborActivitiesSelected() != null && !dto.getLaborActivitiesSelected().isEmpty()) {
            List<LaborActivity> activities = laborActivityRepository.findAllById(dto.getLaborActivitiesSelected());

            if (activities.size() != dto.getLaborActivitiesSelected().size()) {
                throw new NotFoundException("One or more selected labor activities do not exist.");
            }

            jobCard.setLaborActivities(activities);
        }

        if (dto.getAssignedTechniciansSelected() != null && !dto.getAssignedTechniciansSelected().isEmpty()) {
            List<Technician> technicians = technicianRepository.findAllById(dto.getAssignedTechniciansSelected());

            if (technicians.size() != dto.getAssignedTechniciansSelected().size()) {
                throw new NotFoundException("One or more selected technicians do not exist.");
            }

            jobCard.setTechnicians(technicians);
        }

        JobCard savedJobCard = jobCardRepository.save(jobCard);

        savedJobCard.setJobCardNumber(String.format("JC-%06d", savedJobCard.getJobId()));

        try {
            return generateJobCardPdf(savedJobCard);
        } catch (Exception e) {
            throw new PdfGenerationException("Failed to generate job card PDF.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobCardResponseProjection> getAllJobCardPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("jobId").descending()
        );

        return jobCardRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public JobCardResponseDto getJobCardById(Long jobId) {
        return jobCardRepository.findJobCardByJobId(jobId)
                .orElseThrow(() ->
                        new NotFoundException("Job Card not found with ID: " + jobId));
    }

    @Override
    @Transactional
    public byte[] updateJobCard(JobCardModifyRequestDTO dto) {
        JobCard jobCard = jobCardRepository.findByJobId(dto.getJobId())
                .orElseThrow(() -> new NotFoundException("Job Card not found with ID: " + dto.getJobId()));

        JobCardHistory history = new JobCardHistory();
        history.setJobId(jobCard.getJobId());
        history.setJobCardNumber(jobCard.getJobCardNumber());
        history.setVehicleId(jobCard.getVehicle().getVehicleId());
        history.setEstimatedCompletionTime(jobCard.getEstimatedCompletionTime());
        history.setStatus(jobCard.getStatus());
        history.setCustomerComplaintText(jobCard.getCustomerComplaintText());
        history.setCurrentMileage(jobCard.getCurrentMileage());
        history.setActionType("UPDATE");

        jobCardHistoryRepository.save(history);

        if (dto.getLaborActivitiesSelected() != null && !dto.getLaborActivitiesSelected().isEmpty()) {
            List<LaborActivity> laborActivities = laborActivityRepository.findAllById(dto.getLaborActivitiesSelected());

            if (laborActivities.size() != dto.getLaborActivitiesSelected().size()) {
                throw new NotFoundException("One or more selected labor activities do not exist.");
            }

            jobCard.setLaborActivities(laborActivities);
        } else {
            jobCard.setLaborActivities(new ArrayList<>());
        }

        if (dto.getAssignedTechniciansSelected() != null && !dto.getAssignedTechniciansSelected().isEmpty()) {
            List<Technician> technicians = technicianRepository.findAllById(dto.getAssignedTechniciansSelected());

            if (technicians.size() != dto.getAssignedTechniciansSelected().size()) {
                throw new NotFoundException("One or more selected technicians do not exist.");
            }

            jobCard.setTechnicians(technicians);
        } else {
            jobCard.setTechnicians(new ArrayList<>());
        }

        jobCard.setCustomerComplaintText(dto.getCustomerComplaintText());
        jobCard.setCurrentMileage(dto.getCurrentMileage());

        JobCard modifiedJobCard = jobCardRepository.save(jobCard);

        try {
            return generateJobCardPdf(modifiedJobCard);
        } catch (Exception e) {
            throw new PdfGenerationException("Failed to generate job card PDF.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobCardTableViewResponseDTO> searchJobCards(JobCardSearchRequestDTO request, Pageable pageable) {

        if (pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equals("string"))) {

            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdDate").descending()
            );
        }

        LocalDateTime dateFromTime = request.getDateFrom() != null ?
                request.getDateFrom().atStartOfDay() : null;

        LocalDateTime dateToTime = request.getDateTo() != null ?
                request.getDateTo().atTime(23, 59, 59) : null;

        return jobCardRepository.searchJobCardSummaries(
                request.getSearch(),
                request.getStatus(),
                request.getTechnicianId(),
                request.getVehicleRegNo(),
                request.getVehicleVinNo(),
                dateFromTime,
                dateToTime,
                pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getJobCardPdfById(Long jobId) {
        JobCard jobCard = jobCardRepository.findByJobId(jobId)
                .orElseThrow(() ->
                        new NotFoundException("Job Card not found with ID: " + jobId));

        try {
            return generateJobCardPdf(jobCard);
        } catch (Exception e) {
            log.error("Failed to generate PDF for Job Card ID {}: {}", jobId, e.getMessage(), e);
            throw new PdfGenerationException("Failed to generate Job Card PDF.");
        }
    }

    private byte[] generateJobCardPdf(JobCard jobCard) throws Exception {
        // Re-generate the updated document using Thymeleaf and HTML rendering pipeline engine
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Default fallback DTO just in case
        invoiceTempUserDto invoiceTempUserDto = new invoiceTempUserDto(null, "system", "System User");

        // 3. Extract secure details if the principal matches our custom record
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUserPrincipal(
                Long userId, String username, String fullName
        )) {
            invoiceTempUserDto = new invoiceTempUserDto(userId, username, fullName);
        }
        Context context = new Context();
        context.setVariable("jobCard", jobCard);
        context.setVariable("user", invoiceTempUserDto);

        // 1. Process template to a string
        String processedHtml = templateEngine.process("job-card-template", context);

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
}