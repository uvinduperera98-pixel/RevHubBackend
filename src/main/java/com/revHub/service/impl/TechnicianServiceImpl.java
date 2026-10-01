package com.revHub.service.impl;

import com.revHub.dto.request.TechnicianModifyRequestDTO;
import com.revHub.dto.request.TechnicianSaveRequestDTO;
import com.revHub.dto.request.TechnicianSearchRequestDTO;
import com.revHub.dto.response.*;
import com.revHub.entity.Technician;
import com.revHub.repository.TechnicianRepository;
import com.revHub.service.TechnicianService;
import com.revHub.util.StandardResponse;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final ModelMapper modelMapper;

    public TechnicianServiceImpl(TechnicianRepository technicianRepository, ModelMapper modelMapper) {
        this.technicianRepository = technicianRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<StandardResponse> saveTechnicianDetails(TechnicianSaveRequestDTO technicianSaveRequestDTO) {
        Technician technician = modelMapper.map(technicianSaveRequestDTO, Technician.class);
        if (!technicianRepository.existsById(technician.getTechnicianId())) {
            technicianRepository.save(technician);
            return ResponseEntity.ok(new StandardResponse(200, "Success", technician));
        } else {
            throw new DuplicateKeyException("Already added Technician");
        }
    }

    @Override
    public List<TechnicianNameResponseProjection> getAllTechnicianNames() {
        return technicianRepository.findAllTechnicianNames();
    }

    @Override
    public ResponseEntity<StandardResponse> getTechnicianByTechnicianId(Long technicianId) {
        return technicianRepository.findTechnicianByTechnicianId(technicianId)
                .map(technician -> ResponseEntity.ok(new StandardResponse(200, "Success", technician)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StandardResponse(404, "Technician not found", null)));
    }

    @Override
    public ResponseEntity<StandardResponse> updateTechnician(TechnicianModifyRequestDTO technicianModifyRequestDTO) {
        try {
            // 1. Check if the JobCard exists
            Technician technician = technicianRepository.findById(technicianModifyRequestDTO.getTechnicianId())
                    .orElse(null);

            if (technician == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StandardResponse(404, "Technician not found", null));
            }

            // 3. Update the fields
            technician.setTechnicianName(technicianModifyRequestDTO.getTechnicianName());
            technician.setTechnicianContact(technicianModifyRequestDTO.getTechnicianContact());
            technician.setSpeciality(technicianModifyRequestDTO.getSpeciality());
            technicianRepository.save(technician);

            return ResponseEntity.ok(new StandardResponse(200, "Technician updated successfully", null));
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error executing transactional pipeline processing logic: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<StandardResponse> searchTechnicians(TechnicianSearchRequestDTO request, Pageable pageable) {
        try {
            if (pageable.getSort().stream().anyMatch(order -> order.getProperty().equals("string"))) {
                pageable = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("createdDate").descending()
                );
            }

            // Clean up empty strings to null so the repository query works correctly
            String technicianId = (request != null && request.getTechnicianId() != null && !request.getTechnicianId().trim().isEmpty())
                    ? request.getTechnicianId().trim()
                    : null;

            String jobStatus = (request != null && request.getJobStatus() != null && !request.getJobStatus().trim().isEmpty())
                    ? request.getJobStatus().trim()
                    : null;

            Page<TechnicianTableViewResponseProjection> result = technicianRepository.searchTechniciansSummaries(
                    technicianId,
                    jobStatus,
                    pageable
            );

            return ResponseEntity.ok(
                    new StandardResponse(
                            200,
                            "Technicians retrieved successfully!",
                            result
                    )
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<StandardResponse> getAllTechnicianNameList() {
        try {
            List<Object[]> technicianIdsAndNamesList = technicianRepository.findAllTechnicianIdsAndNames();

            // Map to a list of structured objects using Long instead of Long
            List<TechnicianIdNameResponseDto> technicianList = technicianIdsAndNamesList.stream()
                    .map(row -> new TechnicianIdNameResponseDto((Long) row[0], (String) row[1]))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(
                    new StandardResponse(HttpStatus.OK.value(), "Success", technicianList),
                    HttpStatus.OK
            );
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
