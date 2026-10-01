package com.revHub.repository;

import com.revHub.dto.response.InvoiceDashboardResponseProjection;
import com.revHub.dto.response.InvoiceTableViewResponseDTO;
import com.revHub.dto.response.RevenueResponseProjection;
import com.revHub.dto.response.YearlyRevenueResponseProjection;
import com.revHub.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.invoiceDetails WHERE i.invoiceId = :invoiceId")
    Optional<Invoice> findInvoicesByInvoiceId(@Param("invoiceId") Long invoiceId);

    @Query("SELECT i.invoiceId AS invoiceId, " +
            "i.invoiceNumber AS invoiceNumber, " +
            "j.jobId AS jobId, " +
            "i.invoiceDate AS invoiceDate, " +
            "i.grandTotal AS grandTotal, " +
            "i.status AS status, " +
            "c.customerName AS customerName " +
            "FROM Invoice i " +
            "JOIN i.jobCard j " +
            "JOIN j.vehicle v " +
            "JOIN v.customer c " +
            "ORDER BY i.invoiceId DESC")
    List<InvoiceDashboardResponseProjection> findRecentInvoices(Pageable pageable);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceDate >= CURRENT_DATE")
    long countTodayInvoices();

    @Query("SELECT COALESCE(SUM(i.grandTotal), 0.0) FROM Invoice i WHERE CAST(i.invoiceDate AS date) = CURRENT_DATE AND i.status = 'PAID'")
    double getTodayRevenue();

    @Query("SELECT CAST(i.createdDate AS date) AS label, COALESCE(SUM(i.grandTotal), 0.0) AS totalRevenue " +
            "FROM Invoice i " +
            "WHERE i.createdDate >= :startDate AND i.status = 'PAID' " +
            "GROUP BY CAST(i.createdDate AS date) " +
            "ORDER BY label ASC")
    List<RevenueResponseProjection> getWeeklyRevenue(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT CAST(i.createdDate AS date) AS label, COALESCE(SUM(i.grandTotal), 0.0) AS totalRevenue " +
            "FROM Invoice i " +
            "WHERE MONTH(i.createdDate) = MONTH(CURRENT_DATE) AND YEAR(i.createdDate) = YEAR(CURRENT_DATE) AND i.status = 'PAID' " +
            "GROUP BY CAST(i.createdDate AS date) " +
            "ORDER BY label ASC")
    List<RevenueResponseProjection> getMonthlyRevenue();

    @Query("SELECT MONTH(i.createdDate) AS monthKey, COALESCE(SUM(i.grandTotal), 0.0) AS totalRevenue " +
            "FROM Invoice i " +
            "WHERE YEAR(i.createdDate) = YEAR(CURRENT_DATE) AND i.status = 'PAID' " +
            "GROUP BY MONTH(i.createdDate) " +
            "ORDER BY monthKey ASC")
    List<YearlyRevenueResponseProjection> getYearlyRevenue();

    @Query("SELECT new com.revHub.dto.response.InvoiceTableViewResponseDTO(" +
            "c.customerName, " +
            "j.jobId, " +
            "j.jobCardNumber, " +
            "i.invoiceId, " +
            "i.invoiceNumber, " +
            "i.createdUser, " +
            "i.createdDate, " +
            "i.grandTotal, " +
            "i.status) " +
            "FROM Invoice i " +
            "LEFT JOIN i.jobCard j " +
            "LEFT JOIN j.vehicle v " +
            "LEFT JOIN v.customer c " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "   lower(i.invoiceNumber) LIKE lower(concat('%', :search, '%')) OR " +
            "   lower(j.jobCardNumber) LIKE lower(concat('%', :search, '%'))) " +
            "AND (:paymentStatus IS NULL OR :paymentStatus = '' OR i.status = :paymentStatus) " +
            "AND (:dateFrom IS NULL OR i.createdDate >= :dateFrom) " +
            "AND (:dateTo IS NULL OR i.createdDate <= :dateTo)")
    Page<InvoiceTableViewResponseDTO> searchInvoiceSummaries(
            @Param("search") String search,
            @Param("paymentStatus") String paymentStatus,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable
    );

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
            "FROM Invoice i " +
            "WHERE i.jobCard.jobCardNumber = :jobCardNumber " +
            "OR i.jobCard.jobCardNumber LIKE CONCAT('%', :jobCardNumber)")
    boolean existsByJobCardNumberFlexible(@Param("jobCardNumber") String jobCardNumber);
}
