package com.revHub.controller;

import com.revHub.dto.request.JobCardModifyRequestDTO;
import com.revHub.dto.request.JobCardSaveRequestDTO;
import com.revHub.dto.request.JobCardSearchRequestDTO;
import com.revHub.dto.response.JobCardResponseDto;
import com.revHub.dto.response.JobCardResponseProjection;
import com.revHub.dto.response.JobCardTableViewResponseDTO;
import com.revHub.service.JobCardService;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/job-card")
public class JobCardController {
    @Autowired
    JobCardService jobCardService;

    @PostMapping("/save")
    public ResponseEntity<StandardResponse> saveJobCard(@RequestBody JobCardSaveRequestDTO dto) {
        byte[] pdfBytes = jobCardService.saveJobCard(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new StandardResponse(
                        HttpStatus.CREATED.value(),
                        "Job Card Processed Successfully",
                        pdfBytes
                )
        );
    }

    @PutMapping("/modify")
    public ResponseEntity<StandardResponse> updateJobCard(@RequestBody JobCardModifyRequestDTO dto) {

        byte[] pdfBytes = jobCardService.updateJobCard(dto);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Job Card updated successfully",
                        pdfBytes
                )
        );
    }

    @GetMapping("/get-all-job-card")
    public ResponseEntity<StandardResponse> getAllJobCard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<JobCardResponseProjection> response =
                jobCardService.getAllJobCardPaginated(page, size);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Job Cards retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/get-job-card-by-jobId/{jobId}")
    public ResponseEntity<StandardResponse> getJobCardById(@PathVariable Long jobId) {

        JobCardResponseDto response = jobCardService.getJobCardById(jobId);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Job Card retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/get-job-card-pdf-by-jobId/{jobId}")
    public ResponseEntity<StandardResponse> getJobCardPdfById(@PathVariable Long jobId) {

        byte[] pdfBytes = jobCardService.getJobCardPdfById(jobId);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Job Card PDF fetched successfully",
                        pdfBytes
                )
        );
    }

    @PostMapping("/search")
    public ResponseEntity<StandardResponse> searchJobCards(
            @RequestBody JobCardSearchRequestDTO request,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<JobCardTableViewResponseDTO> result = jobCardService.searchJobCards(request, pageable);

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Job Cards retrieved successfully!",
                        result
                )
        );
    }
}
