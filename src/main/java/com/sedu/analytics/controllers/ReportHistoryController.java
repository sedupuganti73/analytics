/**
 * 
 */
package com.bnsf.analytics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.model.ReportHistory;
import com.bnsf.analytics.service.ReportHistoryService;

import groovy.util.logging.Log4j;

/**
 * @author B031526
 *
 */

@Log4j
@RestController
@RequestMapping( "/api/history")
public class ReportHistoryController {
	
	@Autowired
	private ReportHistoryService historyService;
	
	
	@RequestMapping("/{reportId}") 
	public  List<ReportHistory> getReportHistory(@PathVariable("reportId") Long reportId) {
		List<ReportHistory> reportHistoryList = historyService.getHistoryByReport(reportId);
		return reportHistoryList;
	}

}
