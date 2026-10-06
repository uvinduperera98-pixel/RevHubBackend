package com.revHub.controller;

import com.revHub.dto.request.LaborActivityModifyRequestDTO;
import com.revHub.dto.request.LaborActivitySaveRequestDTO;
import com.revHub.dto.response.LaborActivityNameResponseProjection;
import com.revHub.dto.response.LaborActivityTableViewResponseProjection;
import com.revHub.entity.LaborActivity;
import com.revHub.service.LaborActivityService;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/labor-activity")
public class LaborActivityController {

    @Autowired
    LaborActivityService laborActivityService;

    @PostMapping("/save")
    public ResponseEntity<StandardResponse> saveLaborActivityDetails(@RequestBody LaborActivitySaveRequestDTO laborActivitySaveRequestDTO) {
        LaborActivity laborActivity = laborActivityService.saveLaborActivityDetails(laborActivitySaveRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse(HttpStatus.CREATED.value(), "Success", laborActivity));
    }

    @GetMapping("/get-all-labor-activity-names")
    public List<LaborActivityNameResponseProjection> getAllItemNames() {
        return laborActivityService.getAllLaborActivityNames();
    }

    @GetMapping("/get-labor-activity-by-job-number/{jobNumber}")
    public ResponseEntity<StandardResponse> getLaborActivitiesByJobId(@PathVariable String jobNumber) {
        List<LaborActivityNameResponseProjection> laborActivities = laborActivityService.getLaborActivitiesByJobId(jobNumber);

        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Success", laborActivities));
    }

    @GetMapping("/get-labor-activity-by-job-id/{jobId}")
    public ResponseEntity<StandardResponse> getLaborActivitiesByJobId(@PathVariable long jobId) {
        List<LaborActivityNameResponseProjection> laborActivities = laborActivityService.getLaborActivitiesByJobId(jobId);

        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Success", laborActivities));
    }

    @PostMapping("/get-all-labor-activities")
    public ResponseEntity<StandardResponse> getAllLaborActivity(
            @RequestParam(required = false) Long laborActivityId,
            @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<LaborActivityTableViewResponseProjection> laborActivityProjections =
                laborActivityService.getAllLaborActivitiesPaginated(pageable, laborActivityId);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Labor activities retrieved successfully",
                laborActivityProjections
        ));
    }

    @PutMapping("/modify")
    public ResponseEntity<StandardResponse> updateLaborActivity(
            @RequestBody LaborActivityModifyRequestDTO laborActivityModifyRequestDTO) {

        laborActivityService.updateLaborActivity(laborActivityModifyRequestDTO);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Labor Activity updated successfully",
                null
        ));
    }

    @GetMapping("/get-labor-activity-by-laborActivityId/{laborActivityId}")
    public ResponseEntity<StandardResponse> getLaborActivityById(@PathVariable Long laborActivityId) {
        LaborActivityTableViewResponseProjection laborActivity = laborActivityService.getLaborActivityById(laborActivityId);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                laborActivity
        ));
    }
}

