package com.revHub.repository;

import com.revHub.dto.response.CustomerContactNumberEmailIdsResponseDto;
import com.revHub.dto.response.CustomerResponseProjection;
import com.revHub.dto.response.CustomerTableViewProjection;
import com.revHub.entity.Customer;
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
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT c.customerId AS customerId, " +
            "c.customerName AS customerName, " +
            "c.contactNumber AS contactNumber, " +
            "c.drivingLicenseNumber AS drivingLicenseNumber, " +
            "c.email AS email, " +
            "c.customerAddress AS customerAddress, " +
            "c.active AS active " +
            "FROM Customer c " +
            "WHERE c.contactNumber = :contactNumber " +
            "AND c.active = :active")
    Optional<CustomerResponseProjection> findByContactNumberAndActive(String contactNumber, Boolean active);

    @Query(value = "SELECT c.customer_id AS customerId, " +
            "c.customer_name AS customerName, " +
            "c.contact_number AS contactNumber, " +
            "c.driving_license_number AS drivingLicenseNumber, " +
            "c.email AS email, " +
            "c.customer_address AS customerAddress, " +
            "c.active AS active " +
            "FROM customer c " +
            "WHERE c.customer_id = :customerId ",
            nativeQuery = true)
    Optional<CustomerResponseProjection> findByCustomerByCustomerId(Long customerId);

    @Query(value = "SELECT * FROM customer WHERE customer_id = :customerId", nativeQuery = true)
    Optional<Customer> findByCustomerId(@Param("customerId") Long customerId);

    @Query(value = "SELECT COUNT(*) FROM customer WHERE active = 1", nativeQuery = true)
    long countActiveCustomers();

    @Query("SELECT NEW com.revHub.dto.response.CustomerContactNumberEmailIdsResponseDto(" +
            "c.customerId, c.contactNumber, c.email) " +
            "FROM Customer c")
    List<CustomerContactNumberEmailIdsResponseDto> findAllCustomerContactNumberEmailIdsList();

    @Query(value = "SELECT c.customerId AS customerId, c.customerName AS customerName, " +
            "c.contactNumber AS contactNumber, c.email AS email, " +
            "COUNT(j.jobId) AS totalJobs " +
            "FROM Customer c " +
            "LEFT JOIN c.vehicles v " +
            "LEFT JOIN v.jobCards j " +
            "WHERE (:contactNumber IS NULL OR :contactNumber = '' OR c.contactNumber = :contactNumber) " +
            "AND (:email IS NULL OR :email = '' OR c.email = :email) " +
            "AND (:activeStatus IS NULL OR :activeStatus = '' OR CAST(c.active AS string) = :activeStatus) " +
            "GROUP BY c.customerId, c.customerName, c.contactNumber, c.email, c.createdDate",
            countQuery = "SELECT COUNT(DISTINCT c.customerId) FROM Customer c " +
                    "WHERE (:contactNumber IS NULL OR :contactNumber = '' OR c.contactNumber = :contactNumber) " +
                    "AND (:email IS NULL OR :email = '' OR c.email = :email) " +
                    "AND (:activeStatus IS NULL OR :activeStatus = '' OR CAST(c.active AS string) = :activeStatus)")
    Page<CustomerTableViewProjection> findAllCustomerSummaries(
            @Param("contactNumber") String contactNumber,
            @Param("email") String email,
            @Param("activeStatus") String activeStatus,
            Pageable pageable
    );

    boolean existsCustomerByEmailOrContactNumber(String email, String contactNumber);

    boolean existsByEmailAndCustomerIdNot(String email, Long customerId);
}
