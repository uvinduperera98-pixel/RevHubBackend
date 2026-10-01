package com.revHub.repository;

import com.revHub.entity.LaborActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface LaborActivityHistoryRepository extends JpaRepository<LaborActivityHistory, Long> {
}
