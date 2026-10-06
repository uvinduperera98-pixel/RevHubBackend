package com.revHub.controller;

import com.revHub.dto.request.TechnicianModifyRequestDTO;
import com.revHub.dto.request.TechnicianSaveRequestDTO;
import com.revHub.dto.request.TechnicianSearchRequestDTO;
import com.revHub.dto.response.TechnicianNameResponseProjection;
import com.revHub.service.TechnicianService;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/technician")
public class TechnicianController {

    @Autowired
    TechnicianService technicianService;

    @PostMapping("/save")
    public ResponseEntity<StandardResponse> saveTechnicianDetails(@RequestBody TechnicianSaveRequestDTO technicianSaveRequestDTO) {
        return technicianService.saveTechnicianDetails(technicianSaveRequestDTO);
    }

    @GetMapping("/get-all-technician-names")
    public List<TechnicianNameResponseProjection> getAllTechnicianNames() {
        return technicianService.getAllTechnicianNames();
    }

    @PostMapping("/search-all-technicians")
    public ResponseEntity<StandardResponse> searchTechnicians(
            @RequestBody TechnicianSearchRequestDTO request,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return technicianService.searchTechnicians(request, pageable);
    }

    @GetMapping("/get-technician-by-technicianId/{technicianId}")
    public ResponseEntity<StandardResponse> getTechnicianByTechnicianId(@PathVariable Long technicianId) {
        return technicianService.getTechnicianByTechnicianId(technicianId);
    }

    @PutMapping("/modify")
    public ResponseEntity<StandardResponse> updateTechnician(@RequestBody TechnicianModifyRequestDTO technicianModifyRequestDTO) {
        return technicianService.updateTechnician(technicianModifyRequestDTO);
    }

    @GetMapping("/get-all-technician-ids-and-names")
    public ResponseEntity<StandardResponse> getAllTechnicianNameList() {
        return technicianService.getAllTechnicianNameList();
    }
}
