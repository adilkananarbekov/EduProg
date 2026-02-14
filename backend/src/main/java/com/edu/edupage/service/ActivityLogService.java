package com.edu.edupage.service;

import com.edu.edupage.entity.ActivityLog;
import com.edu.edupage.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public void logActivity(String action, String description, Long userId) {
        ActivityLog log = ActivityLog.builder()
                .action(action)
                .description(description)
                .userId(userId)
                .build();
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getRecentActivities() {
        return activityLogRepository.findTop10ByOrderByTimestampDesc();
    }
}
