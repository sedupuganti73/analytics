package com.bnsf.analytics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.service.ReportsService;

import groovy.util.logging.Log4j;


@Log4j
@RestController
@RequestMapping( "/api/reports")
public class ReportsController {
	
		@Autowired
		private ReportsService reportsService;
	
	 	@RequestMapping( "/")
	    public List<Report> getReports() {
	 		return reportsService.getReports();
	    }
	 	
	 	@RequestMapping( value = "/create", method = RequestMethod.POST)
	    public void addReport(
	    			@RequestBody Report report
	    		) {
	 		reportsService.addReport(report);
	 		return;
	    }
	 	
	 	@RequestMapping( value = "/delete/{reportId}", method = RequestMethod.DELETE)
	    public List<Report> deleteReport(
	    			@PathVariable("reportId") Long reportId
	    		) {
	 		reportsService.deleteReport(reportId);
	 		return reportsService.getReports();
	    }
}
