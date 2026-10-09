package com.revHub.repository;

import com.revHub.dto.response.*;
import com.revHub.entity.JobCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface JobCardRepository extends JpaRepository<JobCard, Long> {

    @EntityGraph(attributePaths = {"vehicle", "vehicle.customer"})
    @Query(value = "SELECT j FROM JobCard j " +
            "LEFT JOIN j.vehicle v " +
            "LEFT JOIN v.customer c",
            countQuery = "SELECT COUNT(j) FROM JobCard j")
    Page<JobCardResponseProjection> findAllProjectedBy(Pageable pageable);

    Optional<JobCard> findByJobId(Long jobId);


    @Query("SELECT j FROM JobCard j WHERE j.jobId = :jobId")
    Optional<JobCardResponseDto> findJobCardByJobId(@Param("jobId") Long jobId);

    @Query(value = "SELECT " +
            "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pendingCount, " +
            "SUM(CASE WHEN status = 'IN PROGRESS' THEN 1 ELSE 0 END) AS inProgressCount, " +
            "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelledCount, " +
            "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completedCount " +
            "FROM job_card", nativeQuery = true)
    Map<String, JobCardStatusResponseProjection> getJobCardStatusCounts();

    @Query("SELECT j.jobId AS jobId,j.jobCardNumber AS jobCardNumber, j.status AS status, c.customerName AS customerName " +
            "FROM JobCard j " +
            "JOIN j.vehicle v " +
            "JOIN v.customer c " +
            "ORDER BY j.jobId DESC")
    List<JobCardSummaryResponseProjection> findRecentJobCards(Pageable pageable);

    @Query("SELECT COUNT(j) FROM JobCard j WHERE j.createdDate >= CURRENT_DATE")
    long countTodayJobCards();

    @Query("SELECT DISTINCT new com.revHub.dto.response.JobCardTableViewResponseDTO(" +
            "j.jobId, j.jobCardNumber, c.customerName, v.vehicleRegNo, v.vehicleVinNo, j.createdUser,j.createdDate, j.status, " +
            "t.technicianName) " +
            "FROM JobCard j " +
            "LEFT JOIN j.vehicle v " +
            "LEFT JOIN v.customer c " +
            "LEFT JOIN j.technicians t " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "      lower(j.jobCardNumber) LIKE lower(concat('%', :search, '%')) OR " +
            "      lower(j.customerComplaintText) LIKE lower(concat('%', :search, '%'))) " +
            "AND (:status IS NULL OR :status = '' OR j.status = :status) " +
            "AND (:technicianId IS NULL OR t.technicianId = :technicianId) " +
            "AND (:vehicleRegNo IS NULL OR :vehicleRegNo = '' OR lower(v.vehicleRegNo) LIKE lower(concat('%', :vehicleRegNo, '%'))) " +
            "AND (:vehicleVinNo IS NULL OR :vehicleVinNo = '' OR lower(v.vehicleVinNo) LIKE lower(concat('%', :vehicleVinNo, '%'))) " +
            "AND (:dateFrom IS NULL OR j.createdDate >= :dateFrom) " +
            "AND (:dateTo IS NULL OR j.createdDate <= :dateTo)")
    Page<JobCardTableViewResponseDTO> searchJobCardSummaries(
            @Param("search") String search,
            @Param("status") String status,
            @Param("technicianId") Long technicianId,
            @Param("vehicleRegNo") String vehicleRegNo,
            @Param("vehicleVinNo") String vehicleVinNo,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable);

}
