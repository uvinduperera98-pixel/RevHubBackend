package com.revHub.repository;

import com.revHub.entity.InvoiceDetailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface InvoiceDetailHistoryRepository extends JpaRepository<InvoiceDetailHistory, Long> {
}
