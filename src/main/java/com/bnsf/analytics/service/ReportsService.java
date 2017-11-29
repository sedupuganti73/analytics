package com.bnsf.analytics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.repositories.ReportRepository;

import groovy.util.logging.Log4j;

@Log4j
@Service
public class ReportsService {
	
	@Autowired
	private ReportRepository reportRepository;
	
	public List<Report> getReports()  {
		return reportRepository.findAll();
	}
	
	public Report getReport(Long reportId)  {
		return reportRepository.findOne(reportId);
	}
	
	
	public void addReport(Report report) {
		reportRepository.save(report);
	}
	
	public void deleteReport (Long reportId) {
		reportRepository.delete(reportId);
	}
}
