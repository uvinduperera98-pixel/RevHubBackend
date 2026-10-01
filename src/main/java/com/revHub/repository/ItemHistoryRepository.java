package com.revHub.repository;

import com.revHub.entity.ItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {
}
