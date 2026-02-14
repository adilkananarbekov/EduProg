package com.edu.edupage.repository;

import com.edu.edupage.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // Fetch top 10 most recent logs
    List<ActivityLog> findTop10ByOrderByTimestampDesc();
}
