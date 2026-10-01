package com.revHub.repository;

import com.revHub.dto.response.LaborActivityNameResponseProjection;
import com.revHub.dto.response.LaborActivityTableViewResponseProjection;
import com.revHub.entity.LaborActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface LaborActivityRepository extends JpaRepository<LaborActivity, Long> {

    @Query(value = "SELECT labor_activity_id, activity_name FROM labor_activity", nativeQuery = true)
    List<LaborActivityNameResponseProjection> findAllLaborActivityNames();

    @Query("SELECT la.laborActivityId AS laborActivityId, la.activityName AS activityName " +
            "FROM JobCard jc " +
            "JOIN jc.laborActivities la " +
            "WHERE jc.jobCardNumber = :jobCardNumber")
    List<LaborActivityNameResponseProjection> findLaborActivitiesByJobCardNumber(@Param("jobCardNumber") String jobCardNumber);

    @Query(value = "SELECT la.labor_activity_id AS laborActivityId, " +
            "       la.activity_name AS activityName, " +
            "       la.active AS active " +
            "FROM labor_activity la " +
            "WHERE la.labor_activity_id = :laborActivityId",
            nativeQuery = true)
    Optional<LaborActivityTableViewResponseProjection> findLaborActivityByLaborId(@Param("laborActivityId") Long laborActivityId);

    @Query(value = "SELECT activity_name FROM labor_activity WHERE labor_activity_id = :laborActivityId", nativeQuery = true)
    String findLaborActivityNameByLaborId(@Param("laborActivityId") Long laborActivityId);

    @Query("""
    SELECT l
    FROM LaborActivity l
    WHERE (
        :laborActivityId IS NULL
        OR l.laborActivityId = :laborActivityId
    )
    """)
    Page<LaborActivityTableViewResponseProjection> findAllLaborActivitiesProjectedBy(
            @Param("laborActivityId") Long laborActivityId,
            Pageable pageable
    );

    boolean existsByActivityName(String activityName);

    boolean existsByActivityNameAndLaborActivityIdNot(String activityName, Long laborActivityId);
}
