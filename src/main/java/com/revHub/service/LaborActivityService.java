package com.revHub.service;

import com.revHub.dto.request.LaborActivityModifyRequestDTO;
import com.revHub.dto.request.LaborActivitySaveRequestDTO;
import com.revHub.dto.response.LaborActivityNameResponseProjection;
import com.revHub.dto.response.LaborActivityTableViewResponseProjection;
import com.revHub.entity.LaborActivity;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LaborActivityService {
    LaborActivity saveLaborActivityDetails(LaborActivitySaveRequestDTO laborActivitySaveRequestDTO);

    List<LaborActivityNameResponseProjection> getAllLaborActivityNames();

    List<LaborActivityNameResponseProjection> getLaborActivitiesByJobId(String jobNumber);

    List<LaborActivityNameResponseProjection> getLaborActivitiesByJobId(long jobId);

    void updateLaborActivity(LaborActivityModifyRequestDTO laborActivityModifyRequestDTO);

    LaborActivityTableViewResponseProjection getLaborActivityById(Long laborActivityId);

    Page<LaborActivityTableViewResponseProjection> getAllLaborActivitiesPaginated(Pageable pageable, Long laborActivityId);
}
