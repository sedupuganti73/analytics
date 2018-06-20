package com.bnsf.analytics.jobs;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.service.DataLoadService;
import com.bnsf.analytics.service.ReportsService;

/**
 * @author B031526
 *
 */

@Component 
public class ScheduleTask {
	
	@Autowired
	private DataLoadService loadService;
	
	@Autowired
	private ReportsService reportsService;
	
	
	@Scheduled(cron="0 1 4 * * ?")
	public void loadData() {
		//loadService.loadData();
		
	}
	
	@Scheduled(cron="0 0 0/1 * * ?")
	public void hourlyLoadData() {
		Calendar rightNow = Calendar.getInstance();
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		System.out.println("Current Hour :::::"+ hour);
		List<Report> reportList =reportsService.getReports();
		if (reportList != null && reportList.size() > 0) {
			for (Report report : reportList) {
                if (report.getLoadType() == 1 && report.getRunTime() == hour) {
                	loadService.loadData(report);
				} else if (report.getLoadType() == 2) {
					loadService.loadData(report);
				}
			}
		}
		
	}

}

