package com.bnsf.analytics.utils;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bnsf.analytics.exceptions.DuplicateColumnException;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.model.ReportColumn;

@Component
public class ReportData {
	private static final Logger logger = LoggerFactory.getLogger(ReportData.class);
	private static final String DELIMITER="|";
	private static Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})");
	
	public Set<ReportColumn> getColumns (Connection conn , Report report) throws DuplicateColumnException {
		logger.info("Start : ReportData.getColumns");
		PreparedStatement preparedStmt = null;
		ResultSet result = null;
		Set<ReportColumn> reportColumnList = null;
		try {
			String query = report.getQuery();
			preparedStmt =conn.prepareStatement(query); 
		    result = preparedStmt.executeQuery();
		    reportColumnList = getColumnDefintion(result.getMetaData(), report);
		}  catch (SQLException e) {
			logger.error("ReportData.getColumns", e.getMessage());
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
		PreparedStatement preparedStmt = null;
		ResultSet result = null;
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
		    	if (result.getDate(columnName) != null) {
		    		databuilder.append(result.getDate(columnName));
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
		    } else if ("TimeStamp".equalsIgnoreCase(columnType)) {
		    	column.setType('A');
		    	column.setFormat("yyyy-MM-dd");
		    } else if ("Integer".equalsIgnoreCase(columnType) || "SMALLINT".equalsIgnoreCase(columnType)) {
		    	column.setType('I');
		    	column.setScale(rsmd.getScale(i));
		    	column.setPrecision(rsmd.getPrecision(i));
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
 
}
