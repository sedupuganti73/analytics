package com.bnsf.analytics.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bnsf.analytics.model.ReportHistory;

public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {
	List<ReportHistory> findByReportId(long reportId);
	void deleteByReportId(long reportId);

}
