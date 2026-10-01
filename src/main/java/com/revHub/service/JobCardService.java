package com.revHub.service;

import com.revHub.dto.request.JobCardModifyRequestDTO;
import com.revHub.dto.request.JobCardSaveRequestDTO;
import com.revHub.dto.request.JobCardSearchRequestDTO;
import com.revHub.dto.response.JobCardResponseDto;
import com.revHub.dto.response.JobCardResponseProjection;
import com.revHub.dto.response.JobCardTableViewResponseDTO;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface JobCardService {

    byte[] saveJobCard(JobCardSaveRequestDTO dto);

    byte[] updateJobCard(JobCardModifyRequestDTO dto);

    Page<JobCardResponseProjection> getAllJobCardPaginated(int page, int size);

    JobCardResponseDto getJobCardById(Long jobId);

    Page<JobCardTableViewResponseDTO> searchJobCards(JobCardSearchRequestDTO request, Pageable pageable);

    byte[] getJobCardPdfById(Long jobId);
}
