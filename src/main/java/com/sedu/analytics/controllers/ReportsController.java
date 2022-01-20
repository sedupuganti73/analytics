package com.bnsf.analytics.controllers;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.exceptions.DuplicateColumnException;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.service.DataSourceService;
import com.bnsf.analytics.service.ForceConnectionService;
import com.bnsf.analytics.service.ReportsService;

import groovy.util.logging.Log4j;


@Log4j
@RestController
@RequestMapping( "/api/reports")
public class ReportsController {
	
	    private static final String SHARED_APP="SharedApp";
	
		@Autowired
		private ReportsService reportsService;
		
		@Autowired
		private DataSourceService dsService;
		
		@Autowired
		private ForceConnectionService sfdcDataSourceService;
	
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
	 		report.setSfdcDataSource(sfdcDataSourceService.getDataSource(Long.parseLong( reportObj.get("sfdcDataSource"))));
	 		report.setName(reportObj.get("name"));
	 		report.setQuery(reportObj.get("query"));
	 		report.setCreatedBy(reportObj.get("createdBy"));
	 		String loadType = reportObj.get("type");
	 		if (loadType != null) {
	 			if ("Once".equalsIgnoreCase(loadType)) {
	 				report.setLoadType(0);
	 			} else if ("Daily".equalsIgnoreCase(loadType)) {
	 				report.setLoadType(1);
	 			} else if ("Hourly".equalsIgnoreCase(loadType)) {
	 				report.setLoadType(2);
	 			}
	 		}
	 		
	 		report.setMethod(reportObj.get("methodType"));
	 		report.setIncremental(Boolean.parseBoolean(reportObj.get("isIncremental")));
	 		if (!report.isIncremental()) {
	 			report.setIncrementalValue(null);
	 		}
	 		if (reportObj.get("priority") != null) {
	 			report.setPriority(Integer.parseInt(reportObj.get("priority")));
	 		}
	 		if (reportObj.get("runTime") != null) {
	 			report.setRunTime(Integer.parseInt(reportObj.get("runTime")));
	 		}
	 		if (reportObj.get("recordCountQuery") != null) {
	 			report.setRecordCountQuery(reportObj.get("recordCountQuery"));
	 		}
	 		if (reportObj.get("appName") != null) {
	 			report.setAppName(reportObj.get("appName"));
	 		} else {
	 			report.setAppName(SHARED_APP);
	 		}
	 		if (reportObj.get("flowName") != null) {
	 			report.setFlowName(reportObj.get("flowName"));
	 		}
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
