package com.bnsf.analytics.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bnsf.analytics.exceptions.DuplicateColumnException;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.model.ReportColumn;
import com.bnsf.analytics.model.ReportHistory;
import com.bnsf.analytics.service.ReportHistoryService;
import com.bnsf.analytics.service.ReportsService;
import com.sforce.soap.partner.PartnerConnection;

@Component
public class ReportData {
	private static final Logger logger = LoggerFactory.getLogger(ReportData.class);
	private static final String DELIMITER="|";
	private static Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})");
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String DOUBLE_QUOTES ="\"";
	private static final String OVERWRITE_OPERATION ="Overwrite";
	private static final String APPEND_OPERATION ="Append";
	private static final DateFormat incrementalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
 
	@Value("${spring.data.write-file}")
	private  String writeFile;

	@Autowired
	private LoadToCloud loadToCloud;
	
	@Autowired
	private ReportsService reportsService;
	
	@Autowired
	private ReportHistoryService reportHistoryService;
	
	@Value("${spring.data.file-size}") 
    private int fileSize;
	
	public Set<ReportColumn> getColumns (Connection conn , Report report) throws Exception {
		logger.info("Start : ReportData.getColumns");
		PreparedStatement preparedStmt = null;
		ResultSet result = null;
		Set<ReportColumn> reportColumnList = null;
		try {
			String query = report.getQuery();
			preparedStmt =conn.prepareStatement(query); 
			if (report.isIncremental()) {
				Date runDate =  Calendar.getInstance().getTime();
				preparedStmt.setDate(1, new java.sql.Date(runDate.getTime()));
			}
			preparedStmt.setQueryTimeout(600);
		    result = preparedStmt.executeQuery();
		    reportColumnList = getColumnDefintion(result.getMetaData(), report);
		}  catch (SQLException e) {
			e.printStackTrace(); 
			logger.error("ReportData.getColumns", e.getMessage());
			throw new Exception(e.getMessage()); 
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					logger.error("ReportData.getColumns", e.getMessage());
				}
				result = null;
			}
			if (preparedStmt != null) {
				try {
					preparedStmt.close();
				} catch (SQLException e) {
					logger.error("ReportData.getColumns", e.getMessage());
				}
				preparedStmt = null;
			}
		}
		logger.info("End : ReportData.getColumns");
		return reportColumnList;
	}
	
	public void extractData(Connection conn ,PartnerConnection partnerConnection, Report report,String folderPath, List<ReportColumn> reportColumnList) throws Exception {
		logger.info("Start : ReportData.extractData"); 
		ReportHistory reportHistory =recordReportStart(report,"Started");
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement element : stackTraceElements) {
			System.out.println("************************************************");
			System.out.println("Class Name :: "+element.getClassName());
			System.out.println("Method Name :: "+element.getMethodName());
			System.out.println("************************************************");
		}
		PreparedStatement preparedStmt = null;
		ResultSet result = null;
		String errorMessage = null;
		
		StringBuilder strBuilder = new StringBuilder();
		int partNumber = 1;
		long processedRecordCnt = 0l;
		String data = null;
		String headers = processHeaders(reportColumnList);
		String operation = report.getMethod();
		PrintWriter pw = null;
		 
		try {
			if (report.isIncremental()) {
				loadRunDateOnReport(report);
			}
			String datasetId = loadToCloud.createDataSetDefintion(partnerConnection, report, reportColumnList,operation);
			if (report.getRecordCountQuery() != null && report.getRecordCountQuery().trim().length() > 0) {  
				updateRecordCountBeforeRun(conn,report.getRecordCountQuery(),reportHistory);
			}
			preparedStmt =conn.prepareStatement(report.getQuery());
			if (report.isIncremental()) {
				Date runDate =  incrementalDateFormat.parse(report.getIncrementalValue());
				preparedStmt.setDate(1, new java.sql.Date(runDate.getTime()));
			}
		    result = preparedStmt.executeQuery();
		    strBuilder.append(headers);
		    if (writeFile == null || "true".equalsIgnoreCase(writeFile)) {
			    String reportFileName = report.getName()+".csv";
				pw = new PrintWriter(new File(folderPath,reportFileName));
				pw.write(headers);
				pw.flush();
		    }
		    
			while(result.next()) {
				processedRecordCnt = processedRecordCnt + 1;
				data =processResultSet(result,reportColumnList);
				strBuilder.append(data);
				if (writeFile == null || "true".equalsIgnoreCase(writeFile)) {
				    pw.write(data);
				}
				
			    if(strBuilder.length() > fileSize) {
			    	System.out.println("strBuilder.length()--------->"+ strBuilder.length() +"Part Number ::"+ partNumber);
			    	loadToCloud.publishDataToWave(partnerConnection,strBuilder.toString().getBytes(),datasetId,partNumber);
			    	if (partNumber == 4000) {
			    		System.out.println("Part Number ::"+ partNumber);
			    		if (OVERWRITE_OPERATION.equalsIgnoreCase(report.getMethod())) {
			    			operation = APPEND_OPERATION;
			    		} else {
			    			operation = report.getMethod();
			    		}
					    loadToCloud.processData(datasetId,partnerConnection);
					    datasetId = loadToCloud.createDataSetDefintion(partnerConnection, report, reportColumnList,operation);
					    partNumber = 0;
					}
			    	partNumber++;
			    	strBuilder = null;
			    	strBuilder = new StringBuilder();
			    	//strBuilder.append(headers);
			    }
			}
			if (strBuilder != null && strBuilder.length() > 0) {
				System.out.println("strBuilder.length()--------->"+ strBuilder.length() +"Part Number ::"+ partNumber);
				loadToCloud.publishDataToWave(partnerConnection,strBuilder.toString().getBytes(),datasetId,partNumber);
			}
			loadToCloud.processData(datasetId,partnerConnection);
			if (report.isIncremental()) {
				reportsService.updateReport(report);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ReportData.extractData", e.getMessage());
			logger.error("ReportData.extractData", e.getStackTrace());
			errorMessage = e.getMessage();
			
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					logger.error("ReportData.extractData", e.getMessage());
				}
				result = null;
			}
			if (preparedStmt != null) {
				try {
					preparedStmt.close();
				} catch (SQLException e) {
					logger.error("ReportData.extractData", e.getMessage());
				}
				preparedStmt = null;
			}
			if (pw != null && (writeFile == null || "true".equalsIgnoreCase(writeFile))) {
				pw.flush();
				pw.close();
			}
			recordReportEnd(reportHistory,(errorMessage != null ) ? errorMessage:"Completed" ,processedRecordCnt);
		}
		logger.info("End : ReportData.extractData");
		
	}
	
	private void loadRunDateOnReport(Report report) throws ParseException {
		Date reportParameterDt = null;
		if(report.getIncrementalValue() == null) {
			reportParameterDt = Calendar.getInstance().getTime();
		} else {
			reportParameterDt = incrementalDateFormat.parse(report.getIncrementalValue());  
			if (!reportParameterDt.equals(Calendar.getInstance().getTime())) {
				Calendar c = Calendar.getInstance();
		        c.setTime(reportParameterDt);
		        c.add(Calendar.DATE, 1);
		        reportParameterDt = c.getTime();
			}
		}
		report.setIncrementalValue(incrementalDateFormat.format(reportParameterDt));
	}
	
	private void updateRecordCountBeforeRun(Connection conn,String recordCntQuery,ReportHistory reportHistory ) {
		logger.info("Start : ReportData.updateRecordCountBeforeRun");
		PreparedStatement preparedStmt = null;
		ResultSet result = null;
		StringBuilder strBuilder = new StringBuilder();
		try {
			preparedStmt =conn.prepareStatement(recordCntQuery);
		    result = preparedStmt.executeQuery();
			if(result.next()) {
				reportHistory.setRecordCountBefore(result.getLong("Record_Cnt"));
			
			}
			updateRecordCntBeforeRun(reportHistory);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("ReportData.updateRecordCountBeforeRun", e.getMessage());
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					logger.error("ReportData.updateRecordCountBeforeRun", e.getMessage());
				}
				result = null;
			}
			if (preparedStmt != null) {
				try {
					preparedStmt.close();
				} catch (SQLException e) {
					logger.error("ReportData.updateRecordCountBeforeRun", e.getMessage());
				}
				preparedStmt = null;
			}
		}
		logger.info("End : ReportData.updateRecordCountBeforeRun");
		
	}
	
	/*
	public void extractData (Connection conn , Report report,String folderPath, List<ReportColumn> reportColumnList) throws Exception {
		logger.info("Start : ReportData.extractData");
		PreparedStatement preparedStmt = null;
		ResultSet result = null;
		List<String> dataList = new ArrayList<String>();
		try {
			preparedStmt =conn.prepareStatement(report.getQuery());
		    result = preparedStmt.executeQuery();
		    
			String data = null;
			String reportFileName = report.getName()+".csv";
			PrintWriter pw = new PrintWriter(new File(folderPath,reportFileName));
			pw.write(processHeaders(reportColumnList));
			pw.flush();
			while(result.next()) {
				data =processResultSet(result,reportColumnList);
				pw.write(data);
			}
			pw.flush();
			pw.close();
		} catch (SQLException e) {
			logger.error("ReportData.extractData", e.getMessage());
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					logger.error("ReportData.extractData", e.getMessage());
				}
				result = null;
			}
			if (preparedStmt != null) {
				try {
					preparedStmt.close();
				} catch (SQLException e) {
					logger.error("ReportData.extractData", e.getMessage());
				}
				preparedStmt = null;
			}
		}
		logger.info("End : ReportData.extractData");
		
	}
	*/
	
	private ReportHistory recordReportStart(Report report,String message) {
		logger.info("Start : ReportData.recordReportStart");
		ReportHistory reportHistory = new ReportHistory();
		reportHistory.setReportId(report.getReportId());
		//reportHistory.setStartDate(LocalDateTime.now());
		reportHistory.setStartDate(new Timestamp((new Date()).getTime()));
		reportHistory.setEndDate(new Timestamp((new Date()).getTime()));
		reportHistory.setMessage("Started");
		try {
			reportHistory =reportHistoryService.saveReportHistory(reportHistory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("ReportData.recordReportStart", e.getMessage());
		}
		logger.info("End : ReportData.recordReportStart");
		return reportHistory;
	}
	
	private void updateRecordCntBeforeRun(ReportHistory reportHistory) {
		logger.info("Start : ReportData.updateRecordCntBeforeRun");
		try {
			reportHistory =reportHistoryService.saveReportHistory(reportHistory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("ReportData.updateRecordCntBeforeRun", e.getMessage());
		}
		logger.info("End : ReportData.updateRecordCntBeforeRun");
	}
	private void recordReportEnd(ReportHistory reportHistory, String message, long processedRecordCnt) {
		logger.info("Start : ReportData.recordReportEnd");
		reportHistory.setEndDate(new Timestamp(System.currentTimeMillis()));
		reportHistory.setMessage(message);
		reportHistory.setRecordCountAfter(processedRecordCnt);
		try {
			reportHistory =reportHistoryService.saveReportHistory(reportHistory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("ReportData.recordReportEnd", e.getMessage());
		}
		logger.info("End : ReportData.recordReportEnd");
	}
	private String processHeaders(List<ReportColumn> reportColumnList) {
		logger.info("Start : ReportData.processHeaders");
		StringBuilder databuilder = new StringBuilder();
		System.out.println("Column Count :: "+ reportColumnList.size());
		for (ReportColumn column : reportColumnList) {
			databuilder.append(column.getName());
			databuilder.append(DELIMITER);
		}
		databuilder.trimToSize();
	    databuilder.deleteCharAt(databuilder.lastIndexOf(DELIMITER));
	    databuilder.append('\n');
	    logger.info("End : ReportData.processHeaders");
		return databuilder.toString();
		
	}
	
	private String processResultSet(ResultSet result,List<ReportColumn> reportColumnList) throws Exception {
		logger.info("Start : ReportData.processResultSet");
		StringBuilder databuilder = new StringBuilder();
		String columnName;
		Time dataTime = null;
		for (ReportColumn column : reportColumnList) {
			columnName = column.getName();
			//System.out.println("columnName----::"+ columnName +":::"+ column.getType());
			if (column.getType() == null) {
				throw new Exception("Column data is missing");
			}
			if ('S' == column.getType()) {
				databuilder.append(DOUBLE_QUOTES);
			    databuilder.append(result.getString(columnName));
			    databuilder.append(DOUBLE_QUOTES);
			    databuilder.append(DELIMITER);
		    } else if ('D' == column.getType()) {
		    	 Date columnDate =  result.getDate(columnName);
		    	 databuilder.append(DOUBLE_QUOTES);
		    	 if (columnDate != null) {
		    	     databuilder.append(result.getDate(columnName));
		    	 }
		    	 databuilder.append(DOUBLE_QUOTES);
		    	 databuilder.append(DELIMITER);
		    } else if ('I' == column.getType()) {
		    	databuilder.append(DOUBLE_QUOTES);
		    	databuilder.append(result.getInt(columnName));
		    	databuilder.append(DOUBLE_QUOTES);
		    	databuilder.append(DELIMITER);
		    } else if ('F' == column.getType()) {
		    	databuilder.append(DOUBLE_QUOTES);
		    	    databuilder.append(result.getDouble(columnName));
		    	databuilder.append(DOUBLE_QUOTES);
		    	databuilder.append(DELIMITER);
		    } else if ('T' == column.getType()) {
		    	//dataTime =result.getTime(columnName);
		    	databuilder.append(DOUBLE_QUOTES);
		    	if (result.getTime(columnName) != null) {
		    		
		    		databuilder.append(dateParseRegExp(result.getTime(columnName).toString()));
		    	}
		    	databuilder.append(DOUBLE_QUOTES);
		    	databuilder.append(DELIMITER);
		    } else if ('A' == column.getType()) {
		    	databuilder.append(DOUBLE_QUOTES);
		    	if (result.getTimestamp(columnName) != null) {
		    		databuilder.append(dateFormat.format(result.getTimestamp(columnName)));
		    	}
		    	databuilder.append(DOUBLE_QUOTES);
		    	databuilder.append(DELIMITER);		    	
		    	
		    }
		} 
		databuilder.trimToSize();
	    if (databuilder.length() > 2) {
	    	databuilder.deleteCharAt(databuilder.lastIndexOf(DELIMITER));
	    }
	    databuilder.append("\n");
	    logger.info("End : ReportData.processResultSet");
		return databuilder.toString();
	}
	
	private Set<ReportColumn> getColumnDefintion(ResultSetMetaData rsmd, Report report) throws SQLException, DuplicateColumnException {
		logger.info("Start : ReportData.getColumnDefintion");
		Set<ReportColumn> reportColumnsSet = new HashSet<ReportColumn>();
	    String columnType = null;
	    int precision = 0;
	    int scale  = 0;
		for (int i=1;i<=rsmd.getColumnCount();i++) {
			//columnDefinitionMap.put( rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
			columnType =  rsmd.getColumnTypeName(i);
			ReportColumn column = new ReportColumn();
			column.setReportId(report.getReportId());
			column.setName(rsmd.getColumnName(i));
			column.setLabel(rsmd.getColumnName(i));
			System.out.println("Column Data :::" + column.getName() + "Column Type ::" + columnType);
			
			if ("Char".equalsIgnoreCase(columnType) || "Varchar".equalsIgnoreCase(columnType) || "nVarchar".equalsIgnoreCase(columnType) || "Time".equalsIgnoreCase(columnType)) {
				column.setType('S');
		    } else if ("Date".equalsIgnoreCase(columnType)) {
		    	column.setType('D');
		    	column.setFormat("yyyy-MM-dd");
		    } else if ("TimeStamp".equalsIgnoreCase(columnType) || "datetime".equalsIgnoreCase(columnType)) {
		    	column.setType('A');
		    	column.setFormat("yyyy-MM-dd HH:mm:ss");
		    /*} else if ("Time".equalsIgnoreCase(columnType)) {
		    	column.setType('A');
		    	column.setFormat("HH:mm:ss");*/
		    } else if ("Integer".equalsIgnoreCase(columnType) || "SMALLINT".equalsIgnoreCase(columnType) || "INT".equalsIgnoreCase(columnType) || "BIGINT".equalsIgnoreCase(columnType) || "BYTEINT".equalsIgnoreCase(columnType)) {
		    	column.setType('I');
		    	column.setScale(rsmd.getScale(i));
	    		if (rsmd.getPrecision(i) > 18) {
	    			column.setPrecision(18);
	    		} else {
	    			column.setPrecision(rsmd.getPrecision(i));
	    		}
		    } else if ("Decimal".equalsIgnoreCase(columnType) || "Float".equalsIgnoreCase(columnType)) {
		    	column.setType('F');
		    	column.setScale(rsmd.getScale(i));
		    	column.setPrecision(rsmd.getPrecision(i));
		    	column.setFormat("#,##0.00");
		    } else if ("Time".equalsIgnoreCase(columnType)) {
		    	column.setType('T');
		    	column.setScale(6);
		    	column.setPrecision(11);
		   /* } else {
		    	if (columnType == null) {
		    		if (column.getName().endsWith("_TM")) {
		    			column.setType('T');
		    			column.setScale(rsmd.getScale(1));
				    	column.setPrecision(rsmd.getPrecision(i));
		    		}
		    	} */
		    }
			if(!reportColumnsSet.add(column)) {
				System.out.println("dup col found "+column.getName());
				throw new DuplicateColumnException(column.getName());
			}
		}
				
		logger.info("End : ReportData.getColumnDefintion");
	    return reportColumnsSet;
	}
	
	public static long dateParseRegExp(String period) {
	    Matcher matcher = pattern.matcher(period);
	    if (matcher.matches()) {
	        return Long.parseLong(matcher.group(1)) * 3600000L 
	            + Long.parseLong(matcher.group(2)) * 60000 
	            + Long.parseLong(matcher.group(3)) * 1000 
	            ; 
	    } else {
	        throw new IllegalArgumentException("Invalid format " + period);
	    }
	}
	
	public byte[] getDataByteArray(List<String> dataList) throws IOException {
		byte[] bytes =null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(dataList);
		bytes = baos.toByteArray();
		return bytes;
	}
	
	public  long getBytesForList(List<String> dataList) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(dataList);
		out.close();
		return baos.toByteArray().length;
	}
 
}
