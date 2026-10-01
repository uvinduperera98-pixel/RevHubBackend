package com.revHub.service;

import com.revHub.dto.request.TechnicianModifyRequestDTO;
import com.revHub.dto.request.TechnicianSaveRequestDTO;
import com.revHub.dto.request.TechnicianSearchRequestDTO;
import com.revHub.dto.response.TechnicianNameResponseProjection;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TechnicianService {
    ResponseEntity<StandardResponse> saveTechnicianDetails(TechnicianSaveRequestDTO technicianSaveRequestDTO);

    List<TechnicianNameResponseProjection> getAllTechnicianNames();

    ResponseEntity<StandardResponse> getTechnicianByTechnicianId(Long technicianId);

    ResponseEntity<StandardResponse> updateTechnician(TechnicianModifyRequestDTO technicianModifyRequestDTO);

    ResponseEntity<StandardResponse> searchTechnicians(TechnicianSearchRequestDTO request, Pageable pageable);

    ResponseEntity<StandardResponse> getAllTechnicianNameList();
}
