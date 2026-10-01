package com.revHub.repository;

import com.revHub.entity.JobCardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCardHistoryRepository extends JpaRepository<JobCardHistory, Long> {
}
