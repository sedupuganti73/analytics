package com.bnsf.analytics.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bnsf.analytics.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long>{
	
	
	@Query("SELECT r FROM Report r WHERE r.loadType =2  or (r.loadType = 1 and r.runTime = ?1) ORDER BY r.priority ASC")
	List<Report> findReportsToRun(Integer runTime);
	
	
	@Query("SELECT r FROM Report r WHERE r.name=?1  ORDER BY r.name ASC")
	Report findReportByName(String name);
	
	
}
