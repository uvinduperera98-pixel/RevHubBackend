package com.revHub.service.impl;

import com.revHub.dto.request.LaborActivityModifyRequestDTO;
import com.revHub.dto.request.LaborActivitySaveRequestDTO;
import com.revHub.dto.response.LaborActivityNameResponseProjection;
import com.revHub.dto.response.LaborActivityTableViewResponseProjection;
import com.revHub.entity.LaborActivity;
import com.revHub.entity.LaborActivityHistory;
import com.revHub.exception.BadRequestException;
import com.revHub.exception.DuplicateException;
import com.revHub.exception.NotFoundException;
import com.revHub.repository.InvoiceRepository;
import com.revHub.repository.LaborActivityHistoryRepository;
import com.revHub.repository.LaborActivityRepository;
import com.revHub.service.LaborActivityService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class LaborActivityServiceImpl implements LaborActivityService {

    private final LaborActivityRepository laborActivityRepository;
    private final LaborActivityHistoryRepository laborActivityHistoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper modelMapper;

    public LaborActivityServiceImpl(LaborActivityRepository laborActivityRepository, LaborActivityHistoryRepository laborActivityHistoryRepository, InvoiceRepository invoiceRepository, ModelMapper modelMapper) {
        this.laborActivityRepository = laborActivityRepository;
        this.laborActivityHistoryRepository = laborActivityHistoryRepository;
        this.invoiceRepository = invoiceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public LaborActivity saveLaborActivityDetails(LaborActivitySaveRequestDTO laborActivitySaveRequestDTO) {

        LaborActivity laborActivity = modelMapper.map(laborActivitySaveRequestDTO, LaborActivity.class);

        if (laborActivityRepository.existsByActivityName(laborActivity.getActivityName())) {
            throw new DuplicateException("Already added Labor Activity Name");
        }

        return laborActivityRepository.save(laborActivity);
    }

    @Override
    public List<LaborActivityNameResponseProjection> getAllLaborActivityNames() {
        return laborActivityRepository.findAllLaborActivityNames();
    }

    @Override
    public List<LaborActivityNameResponseProjection> getLaborActivitiesByJobId(String jobNumber) {

        // 1. Validate input
        if (jobNumber == null || jobNumber.trim().isEmpty()) {
            throw new BadRequestException("Job card number cannot be null or empty");
        }

        String trimmedInput = jobNumber.trim();

        // 2. Remove JC- prefix if provided
        String numericPart = trimmedInput.startsWith("JC-")
                ? trimmedInput.substring(3)
                : trimmedInput;

        // 3. Validate numeric part
        if (!numericPart.matches("^[0-9]+$")) {
            throw new BadRequestException("Job card number must contain numbers only.");
        }

        // 4. Ensure JC- prefix
        String jobCardNumber = trimmedInput.startsWith("JC-")
                ? trimmedInput
                : "JC-" + trimmedInput;

        // 5. Check whether invoice already exists
        boolean hasInvoice = invoiceRepository.existsByJobCardNumberFlexible(jobCardNumber);

        if (hasInvoice) {
            log.warn("Attempted to fetch labor activities for job card {} which already has an invoice.", jobCardNumber);
            throw new DuplicateException("Job card has an invoice already.");
        }

        // 6. Fetch labor activities
        List<LaborActivityNameResponseProjection> laborActivities =
                laborActivityRepository.findLaborActivitiesByJobCardNumber(jobCardNumber);

        return laborActivities;
    }

    @Override
    public List<LaborActivityNameResponseProjection> getLaborActivitiesByJobId(long jobId) {

        // 5. Check whether invoice already exists
        boolean hasInvoice = invoiceRepository.existsByJobId(jobId);

        if (hasInvoice) {
            log.warn("Attempted to fetch labor activities for job card {} which already has an invoice.", jobId);
            throw new DuplicateException("Job card has an invoice already.");
        }

        // 6. Fetch labor activities
        List<LaborActivityNameResponseProjection> laborActivities =
                laborActivityRepository.findLaborActivitiesByJobCardNumber(jobId);

        return laborActivities;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLaborActivity(LaborActivityModifyRequestDTO laborActivityModifyRequestDTO) {

        LaborActivity laborActivity = laborActivityRepository.findById(
                laborActivityModifyRequestDTO.getLaborActivityId()
        ).orElseThrow(() -> new NotFoundException("Labor Activity not found"));

        boolean nameExists = laborActivityRepository.existsByActivityNameAndLaborActivityIdNot(
                laborActivityModifyRequestDTO.getActivityName(),
                laborActivityModifyRequestDTO.getLaborActivityId()
        );

        if (nameExists) {
            throw new DuplicateException("Labor Activity name already exists");
        }

        LaborActivityHistory history = new LaborActivityHistory();
        history.setLaborActivityId(laborActivity.getLaborActivityId());
        history.setActivityName(laborActivity.getActivityName());
        history.setHourlyRate(laborActivity.getHourlyRate());
        history.setFlatRateCharge(laborActivity.getFlatRateCharge());
        history.setEstimatedDurationHours(laborActivity.getEstimatedDurationHours());
        history.setActive(laborActivity.getActive());
        history.setActionType("UPDATE");

        laborActivityHistoryRepository.save(history);

        laborActivity.setActivityName(laborActivityModifyRequestDTO.getActivityName());
        laborActivity.setActive(laborActivityModifyRequestDTO.getActive());

        laborActivityRepository.save(laborActivity);
    }

    @Override
    public LaborActivityTableViewResponseProjection getLaborActivityById(Long laborActivityId) {
        return laborActivityRepository.findLaborActivityByLaborId(laborActivityId)
                .orElseThrow(() -> new NotFoundException("Labor Activity not found"));
    }

    @Override
    public Page<LaborActivityTableViewResponseProjection> getAllLaborActivitiesPaginated(
            Pageable pageable,
            Long laborActivityId) {

        if (pageable.getSort().stream().anyMatch(order -> order.getProperty().equalsIgnoreCase("string"))) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdDate").descending()
            );
        }

        return laborActivityRepository.findAllLaborActivitiesProjectedBy(
                laborActivityId,
                pageable
        );
    }
}
