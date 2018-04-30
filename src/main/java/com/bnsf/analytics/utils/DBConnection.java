/**
 * 
 */
package com.bnsf.analytics.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author B031526
 *
 */

@Component 
public class DBConnection {
	
	private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
	
	private String databaseType;
	private String userName;
	private String password;
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public Connection getConntection(String url, String userName, String password) throws SQLException, ClassNotFoundException {
		logger.info("Start : DBConnection.getConntection", url, userName);
		Connection connection = null;
		try {
		    connection = DriverManager.getConnection(url, userName, password);
		} catch(Exception ex) {
			logger.error("DBConnection.getConntection", ex.getMessage());
		}
		logger.info("End : DBConnection.getConntection", url, userName);
		return connection;
	}
	
	
	public void closeConnection(Connection connection) {
		logger.info("Start : DBConnection.closeConnection");
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("DBConnection.getConntection", e.getMessage());
			}
			connection = null;
		}
		logger.info("End : DBConnection.closeConnection");
	}
	
	
	

}
