package com.revHub.repository;

import com.revHub.entity.InvoiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface InvoiceHistoryRepository extends JpaRepository<InvoiceHistory, Long> {

}
