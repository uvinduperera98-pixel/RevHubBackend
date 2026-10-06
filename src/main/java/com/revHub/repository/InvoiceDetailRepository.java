package com.revHub.repository;

import com.revHub.dto.response.TopLaborActivityResponseProjection;
import com.revHub.entity.InvoiceDetail;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {

    @Query("SELECT l.activityName AS activityName, COUNT(d) AS activityCount " +
            "FROM InvoiceDetail d JOIN d.invoice i, LaborActivity l " +
            "WHERE d.laborActivityId = l.laborActivityId " +
            "AND i.status = 'PAID' AND d.type = 'LABOR' " +
            "GROUP BY l.activityName " +
            "ORDER BY activityCount DESC")
    List<TopLaborActivityResponseProjection> findTopLaborActivities(Limit limit);
}
