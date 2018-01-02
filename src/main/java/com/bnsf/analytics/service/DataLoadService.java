package com.bnsf.analytics.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnsf.analytics.exceptions.DuplicateColumnException;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.model.ReportColumn;
import com.bnsf.analytics.utils.DBConnection;
import com.bnsf.analytics.utils.LoadToCloud;
import com.bnsf.analytics.utils.ReportData;
import com.bnsf.analytics.utils.Utility;
import com.bnsf.analytics.model.DataSource;

@Service
public class DataLoadService {


	private static final String FOLDER_PATH ="C:\\Users\\B031526\\Downloads\\dataFolder";

	
	
	@Autowired
	private DBConnection connection;
	
	@Autowired
	private ReportsService reportsService;
	
		
	@Autowired
	private ColumnsService columnsService;
	
	@Autowired
	private ReportData reportData;
	@Autowired
	private LoadToCloud loadData;
	
	
	@Autowired
	private Utility utils;

	
	public Set<ReportColumn>  getColumns(Report report) throws ClassNotFoundException, SQLException, DuplicateColumnException  {
		Connection  dbConnection = null;
		Set<ReportColumn> columns = null;
		DataSource datasource = report.getDataSource();
		try {
			dbConnection =connection.getConntection(datasource.getUrl(), datasource.getDbUsername(), datasource.getDbPassword());
			columns = reportData.getColumns(dbConnection, report); 	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection.closeConnection(dbConnection);
		}
		return columns;
	}
	
	
	public void  loadData()  {
		//utils.getConnection();
		List<Report> reportList =reportsService.getReports();
		if (reportList != null && reportList.size() > 0) {
			List<ReportColumn> reportColumnList = null;
			Connection  dbConnection = null;
			String reportdfolderPath = null;
			DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
			for (Report report : reportList) {
				try {
					DataSource datasource = report.getDataSource();
					reportColumnList = columnsService.getColumns(report.getReportId());
					System.out.println(report.getName() +"Start Time:: "+ df.format(Calendar.getInstance().getTime()));
					reportdfolderPath =createReportFolder(report.getName(),FOLDER_PATH);
					dbConnection =connection.getConntection(datasource.getUrl(), datasource.getDbUsername(), datasource.getDbPassword());
					reportData.extractData(dbConnection, report,reportdfolderPath,reportColumnList);
					loadData.processLoad(reportdfolderPath, reportColumnList, report);
					System.out.println(report.getName() +"End Time:: "+ df.format(Calendar.getInstance().getTime()));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					connection.closeConnection(dbConnection);
				}
				
			}
			
		}
				 

	}

	
	private  boolean isJSONValid(String jsonInString ) {
		boolean isValid = false;
	    try {
	       final ObjectMapper mapper = new ObjectMapper();
	       mapper.readTree(jsonInString);
	       isValid =true;
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
	    
	    return isValid;
	  }
	
	
	
	private String createReportFolder(String reportName, String filePath) {
		Date today = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyyy-mm-dd");
		StringBuilder reportFolderBuilder = new StringBuilder();
		reportFolderBuilder.append(reportName);
		reportFolderBuilder.append("-");
		reportFolderBuilder.append(df.format(today));
		File reportFolder= new File(filePath, reportFolderBuilder.toString()); 
		if (!reportFolder.exists()) {
			reportFolder.mkdirs();
		}
		
		return reportFolder.getAbsolutePath();
	}

}
