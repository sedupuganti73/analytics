package com.bnsf.analytics.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
import com.sforce.soap.partner.PartnerConnection;

@Component
public class ReportData {
	private static final Logger logger = LoggerFactory.getLogger(ReportData.class);
	private static final String DELIMITER="|";
	private static Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})");
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 
	@Autowired
	private LoadToCloud loadToCloud;
	
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
	
	public void extractData (Connection conn , Report report,String folderPath, List<ReportColumn> reportColumnList) throws Exception {
		logger.info("Start : ReportData.extractData");
		ReportHistory reportHistory =recordReportStart(report,"Started");
		System.out.println("reportHistory--------->"+ reportHistory);
		PreparedStatement preparedStmt = null;
		ResultSet result = null;
		PartnerConnection partnerConnection = null;
		String errorMessage = null;
		
		StringBuilder strBuilder = new StringBuilder();
		partnerConnection = loadToCloud.getConnection();
		String datasetId = loadToCloud.createDataSetDefintion(partnerConnection, report, reportColumnList);
		int partNumber = 1;
		long processedRecordCnt = 0l;
		String headers = processHeaders(reportColumnList);
		try {
			if (report.getRecordCountQuery() != null && report.getRecordCountQuery().trim().length() > 0) {
				updateRecordCountBeforeRun(conn,report.getRecordCountQuery(),reportHistory);
			}
			preparedStmt =conn.prepareStatement(report.getQuery());
		    result = preparedStmt.executeQuery();
		    strBuilder.append(headers);
		    System.out.println("File Size::"+ fileSize);
		    
			while(result.next()) {
				processedRecordCnt = processedRecordCnt + 1;
				strBuilder.append(processResultSet(result,reportColumnList));
			    if(strBuilder.length() > fileSize) {
			    	System.out.println("strBuilder.length()--------->"+ strBuilder.length());
			    	loadToCloud.publishDataToWave(partnerConnection,strBuilder.toString().getBytes(),datasetId,partNumber);
			    	partNumber++;
			    	strBuilder = null;
			    	strBuilder = new StringBuilder();
			    	strBuilder.append(headers);
			    }
			
			}
			if (strBuilder != null && strBuilder.length() > 0) {
				System.out.println("strBuilder.length()--------->"+ strBuilder.length());
				loadToCloud.publishDataToWave(partnerConnection,strBuilder.toString().getBytes(),datasetId,partNumber);
			}
			loadToCloud.processData(datasetId,partnerConnection);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("ReportData.extractData", e.getMessage());
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
			recordReportEnd(reportHistory,(errorMessage != null ) ? errorMessage:"Completed" ,processedRecordCnt);
		}
		logger.info("End : ReportData.extractData");
		
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
		reportHistory.setStartDate(LocalDateTime.now());
		reportHistory.setEndDate(LocalDateTime.now());
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
		reportHistory.setEndDate(LocalDateTime.now());
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
			    databuilder.append(result.getString(columnName));
			    databuilder.append(DELIMITER);
		    } else if ('D' == column.getType()) {
		    	 Date columnDate =  result.getDate(columnName);
		    	 if (columnDate != null) {
		    	     databuilder.append(result.getDate(columnName));
		    	 }
		    	 databuilder.append(DELIMITER);
		    	 
		    } else if ('I' == column.getType()) {
		    	databuilder.append(result.getInt(columnName));
		    	databuilder.append(DELIMITER);
		    } else if ('F' == column.getType()) {
		    	databuilder.append(result.getDouble(columnName));
		    	databuilder.append(DELIMITER);
		    } else if ('T' == column.getType()) {
		    	//dataTime =result.getTime(columnName);
		    	if (result.getTime(columnName) != null) {
		    		
		    		databuilder.append(dateParseRegExp(result.getTime(columnName).toString()));
		    	}
		    	databuilder.append(DELIMITER);
		    } else if ('A' == column.getType()) {
		    	if (result.getTimestamp(columnName) != null) {
		    		databuilder.append(dateFormat.format(result.getTimestamp(columnName)));
		    	}
		    	databuilder.append(DELIMITER);		    	
		    	
		    }
		} 
		databuilder.trimToSize();
	    if (databuilder.length() > 2) {
	    	databuilder.deleteCharAt(databuilder.lastIndexOf(DELIMITER));
	    }
	    databuilder.append('\n');
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
			
			if ("Char".equalsIgnoreCase(columnType) || "Varchar".equalsIgnoreCase(columnType)) {
				column.setType('S');
		    } else if ("Date".equalsIgnoreCase(columnType)) {
		    	column.setType('D');
		    	column.setFormat("yyyy-MM-dd");
		    } else if ("TimeStamp".equalsIgnoreCase(columnType) || "datetime".equalsIgnoreCase(columnType)) {
		    	column.setType('A');
		    	column.setFormat("yyyy-MM-dd HH:mm:ss");
		    } else if ("Integer".equalsIgnoreCase(columnType) || "SMALLINT".equalsIgnoreCase(columnType) || "INT".equalsIgnoreCase(columnType) || "BIGINT".equalsIgnoreCase(columnType)) {
		    	column.setType('I');
		    	column.setScale(rsmd.getScale(i));
	    		if (rsmd.getPrecision(i) > 18) {
	    			column.setPrecision(18);
	    		} else {
	    			column.setPrecision(rsmd.getPrecision(i));
	    		}
		    } else if ("Decimal".equalsIgnoreCase(columnType)) {
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
