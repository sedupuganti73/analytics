package com.bnsf.analytics.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;

import org.springframework.stereotype.Component;

import com.bnsf.analytics.exceptions.DuplicateColumnException;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.model.ReportColumn;

@Component
public class ReportData {
	private static final String DELIMITER="|";
	
	public Set<ReportColumn> getColumns (Connection conn , Report report) throws DuplicateColumnException {
		PreparedStatement preparedStmt = null;
		ResultSet result = null;
		Set<ReportColumn> reportColumnList = null;
		try {
			String query = report.getQuery();
			//query = query.replaceFirst("SELECT", "SELECT TOP 1");
			//query = query.replaceFirst("select", "SELECT TOP 1");
			preparedStmt =conn.prepareStatement(query);
		    result = preparedStmt.executeQuery();
		    reportColumnList = getColumnDefintion(result.getMetaData(), report);
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result = null;
			}
			if (preparedStmt != null) {
				try {
					preparedStmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				preparedStmt = null;
			}
		}
		return reportColumnList;
	}
	
	public void extractData (Connection conn , Report report,String folderPath, List<ReportColumn> reportColumnList) throws FileNotFoundException {
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
				//System.out.println("Data :: "+ data);
				pw.write(data);
			}
			pw.flush();
			pw.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result = null;
			}
			if (preparedStmt != null) {
				try {
					preparedStmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				preparedStmt = null;
			}
		}
		
	}
	
	private String processHeaders(List<ReportColumn> reportColumnList) {
		StringBuilder databuilder = new StringBuilder();
		for (ReportColumn column : reportColumnList) {
			databuilder.append(column.getName());
			databuilder.append(DELIMITER);
		}
		databuilder.trimToSize();
	    databuilder.deleteCharAt(databuilder.lastIndexOf(DELIMITER));
	    databuilder.append('\n');
	    System.out.println("Headers ---->"+ databuilder.toString());
		return databuilder.toString();
		
	}
	
	private String processResultSet(ResultSet result,List<ReportColumn> reportColumnList) throws SQLException {
		StringBuilder databuilder = new StringBuilder();
		String columnName;
		for (ReportColumn column : reportColumnList) {
			columnName = column.getName();
			if ('S' == column.getType()) {
			    databuilder.append(result.getString(columnName));
			    databuilder.append(DELIMITER);
		    } else if ('D' == column.getType()) {
		    	 databuilder.append(result.getDate(columnName));
		    	 databuilder.append(DELIMITER);
		    } else if ('I' == column.getType()) {
		    	databuilder.append(result.getInt(columnName));
		    	databuilder.append(DELIMITER);
		    } else if ('F' == column.getType()) {
		    	databuilder.append(result.getDouble(columnName));
		    	databuilder.append(DELIMITER);
		    
		    }
		} 
		databuilder.trimToSize();
	    if (databuilder.length() > 2) {
	    	databuilder.deleteCharAt(databuilder.lastIndexOf(DELIMITER));
	    }
	    databuilder.append('\n');
		return databuilder.toString();
	}
	
	private Set<ReportColumn> getColumnDefintion(ResultSetMetaData rsmd, Report report) throws SQLException, DuplicateColumnException {
		Set<ReportColumn> reportColumnsSet = new HashSet<ReportColumn>();
	    String columnType = null;
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
		    } else if ("Integer".equalsIgnoreCase(columnType) ) {
		    	column.setType('I');
		    	column.setScale(rsmd.getScale(1));
		    	column.setPrecision(rsmd.getPrecision(i));
		    } else if ("Decimal".equalsIgnoreCase(columnType)) {
		    	column.setType('F');
		    	column.setScale(rsmd.getScale(1));
		    	column.setPrecision(rsmd.getPrecision(i));
		    	column.setFormat("#,##0.00");
		    }
			if(!reportColumnsSet.add(column)) {
				System.out.println("dup col found "+column.getName());
				throw new DuplicateColumnException(column.getName());
			}
		}
				
	    
	    return reportColumnsSet;
	}
 
}
