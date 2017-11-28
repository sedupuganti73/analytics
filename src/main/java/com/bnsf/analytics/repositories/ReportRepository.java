package com.bnsf.analytics.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.bnsf.analytics.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long>{
}
