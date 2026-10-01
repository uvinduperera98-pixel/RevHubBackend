package com.revHub.repository;

import com.revHub.dto.response.TechnicianNameResponseProjection;
import com.revHub.dto.response.TechnicianResponseProjection;
import com.revHub.dto.response.TechnicianTableViewResponseProjection;
import com.revHub.entity.Technician;
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
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    @Query(value = "SELECT technician_id, technician_name FROM technician", nativeQuery = true)
    List<TechnicianNameResponseProjection> findAllTechnicianNames();

    @Query("SELECT t FROM Technician t WHERE t.technicianId = :technicianId")
    Optional<TechnicianResponseProjection> findTechnicianByTechnicianId(@Param("technicianId") Long technicianId);

    @Query(value = """
    SELECT t.technicianId AS technicianId,
           t.technicianName AS technicianName,
           u.username AS username,
           u.email AS email,
           t.speciality AS speciality,
           (SELECT COUNT(j) FROM JobCard j JOIN j.technicians tj WHERE tj.technicianId = t.technicianId AND j.status = 'PENDING') AS activeJobCount,
           CASE WHEN (SELECT COUNT(j) FROM JobCard j JOIN j.technicians tj WHERE tj.technicianId = t.technicianId AND j.status = 'PENDING') > 0 THEN 'Busy'
                ELSE 'Available'
           END AS jobStatus,
           t.status AS status
    FROM Technician t
    JOIN t.user u
    JOIN u.roles r
    WHERE u.active = true
      AND r.roleName = 'TECHNICIAN'
      AND (:technicianId IS NULL OR :technicianId = '' OR CAST(t.technicianId AS string) LIKE %:technicianId% OR LOWER(u.username) LIKE LOWER(CONCAT('%', :technicianId, '%')) OR LOWER(t.technicianName) LIKE LOWER(CONCAT('%', :technicianId, '%')))
      AND (:jobStatus IS NULL OR :jobStatus = '' OR (
            CASE WHEN (SELECT COUNT(j) FROM JobCard j JOIN j.technicians tj WHERE tj.technicianId = t.technicianId AND j.status = 'PENDING') > 0 THEN 'Busy'
                 ELSE 'Available'
            END
          ) = :jobStatus)
    """,
            countQuery = """
    SELECT COUNT(t)
    FROM Technician t
    JOIN t.user u
    JOIN u.roles r
    WHERE u.active = true
      AND r.roleName = 'TECHNICIAN'
      AND (:technicianId IS NULL OR :technicianId = '' OR CAST(t.technicianId AS string) LIKE %:technicianId% OR LOWER(u.username) LIKE LOWER(CONCAT('%', :technicianId, '%')) OR LOWER(t.technicianName) LIKE LOWER(CONCAT('%', :technicianId, '%')))
      AND (:jobStatus IS NULL OR :jobStatus = '' OR (
            CASE WHEN (SELECT COUNT(j) FROM JobCard j JOIN j.technicians tj WHERE tj.technicianId = t.technicianId AND j.status = 'PENDING') > 0 THEN 'Busy'
                 ELSE 'Available'
            END
          ) = :jobStatus)
    """)
    Page<TechnicianTableViewResponseProjection> searchTechniciansSummaries(
            @Param("technicianId") String technicianId,
            @Param("jobStatus") String jobStatus,
            Pageable pageable
    );

    @Query("SELECT t.technicianId, u.username FROM Technician t JOIN t.user u JOIN u.roles r WHERE u.active = true AND r.roleName = 'TECHNICIAN' AND u.username IS NOT NULL AND TRIM(u.username) <> '' AND u.username <> 'N/A'")
    List<Object[]> findAllTechnicianIdsAndNames();

}
