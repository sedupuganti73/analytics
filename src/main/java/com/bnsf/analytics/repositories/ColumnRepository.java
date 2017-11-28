package com.bnsf.analytics.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bnsf.analytics.model.ReportColumn;

public interface ColumnRepository extends JpaRepository<ReportColumn, Long>{
	List<ReportColumn> findByReportId(long reportId);
}
