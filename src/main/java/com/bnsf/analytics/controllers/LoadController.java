package com.bnsf.analytics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.model.DataSource;
import com.bnsf.analytics.service.ColumnsService;
import com.bnsf.analytics.service.DataLoadService;
import com.bnsf.analytics.service.ReportsService;

import groovy.util.logging.Log4j;

@Log4j
@RestController
@RequestMapping( "/api/load")
public class LoadController {
	
	@Autowired
	private DataLoadService dataLoadService;
	
	@Autowired
	private ReportsService reportService;
	
	@RequestMapping( "/")
    public void runAllReports() {
 		 dataLoadService.loadData();
    }
	
	@RequestMapping("/{reportId}") 
	public void runReport(@PathVariable("reportId") Long reportId) {
		dataLoadService.loadData(reportService.getReport(reportId));
	}

}
