package com.bnsf.analytics.controllers;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.exceptions.DuplicateColumnException;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.service.DataSourceService;
import com.bnsf.analytics.service.ReportsService;

import groovy.util.logging.Log4j;


@Log4j
@RestController
@RequestMapping( "/api/reports")
public class ReportsController {
	
		@Autowired
		private ReportsService reportsService;
		
		@Autowired
		private DataSourceService dsService;
	
	 	@RequestMapping( "/")
	    public List<Report> getReports() {
	 		return reportsService.getReports();
	    }
	 	
	 	@RequestMapping( "/{reportId}")
	    public Report getReport(@PathVariable("reportId") Long reportId) {
	 		return reportsService.getReport(reportId);
	    }
	 	
	 	
	 	@RequestMapping( value = "/", method = RequestMethod.POST)
	    public Report addReport(
	    			@RequestBody LinkedHashMap<String, String> reportObj
	    		) throws DuplicateColumnException {
	 		Report report  = new Report();
	 		if(reportObj.get("reportId")!=null) {
	 			report.setReportId(Long.parseLong( reportObj.get("reportId"))); 
	 		}
	 		report.setDataSource(dsService.getDataSource(Long.parseLong( reportObj.get("dataSource"))) );
	 		report.setName(reportObj.get("name"));
	 		report.setQuery(reportObj.get("query"));
	 		report.setCreatedBy(reportObj.get("createdBy"));
	 		reportsService.addReport(report);
	 		return report;
	    }
	 	
	 	@RequestMapping( value = "/delete/{reportId}", method = RequestMethod.DELETE)
	    public List<Report> deleteReport(
	    			@PathVariable("reportId") Long reportId
	    		) {
	 		reportsService.deleteReport(reportId);
	 		return reportsService.getReports();
	    }
}
