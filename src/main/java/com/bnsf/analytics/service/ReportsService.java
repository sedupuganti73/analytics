package com.bnsf.analytics.service;

import java.sql.SQLException;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.model.ReportColumn;
import com.bnsf.analytics.repositories.ColumnRepository;
import com.bnsf.analytics.repositories.ReportRepository;

import groovy.util.logging.Log4j;

@Log4j
@Service
public class ReportsService {
	
	@Autowired
	private ReportRepository reportRepository;
	
	@Autowired
	private DataLoadService dataService;

	@Autowired
	private ColumnRepository columnRepository;
	
	public List<Report> getReports()  {
		return reportRepository.findAll();
	}
	
	public Report getReport(Long reportId)  {
		return reportRepository.findOne(reportId);
	}
	
	
	public void addReport(Report report) {
		report = reportRepository.save(report);
		try {
			List<ReportColumn> columns = dataService.getColumns(report);
			columns.forEach((column)->columnRepository.save(column));
			
		} catch(SQLException se) {
			se.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Transactional
	public void deleteReport (Long reportId) {
		reportRepository.delete(reportId);
		columnRepository.deleteByReportId(reportId);
	}
}
