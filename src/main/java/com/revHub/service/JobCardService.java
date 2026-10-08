package com.revHub.service;

import com.revHub.dto.request.JobCardModifyRequestDTO;
import com.revHub.dto.request.JobCardSaveRequestDTO;
import com.revHub.dto.request.JobCardSearchRequestDTO;
import com.revHub.dto.response.*;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface JobCardService {

    PdfPreviewResponseDTO saveJobCard(JobCardSaveRequestDTO dto);

    PdfPreviewResponseDTO updateJobCard(JobCardModifyRequestDTO dto);

    Page<JobCardResponseProjection> getAllJobCardPaginated(int page, int size);

    JobCardResponseDto getJobCardById(Long jobId);

    Page<JobCardTableViewResponseDTO> searchJobCards(JobCardSearchRequestDTO request, Pageable pageable);

    PdfPreviewResponseDTO getJobCardPdfById(Long jobId);

    List<JobCardNumberResponseDTO> getPendingJobNumbers();
}
