/**
 * 
 */
package com.bnsf.analytics.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

/**
 * @author B031526
 *
 */

@Component 
public class DBConnection {
	
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
	
	
	public Connection getConntection() throws SQLException, ClassNotFoundException {
		Connection connection = null;
		///Class.forName("com.ncr.teradata.TeraDriver");
		connection = DriverManager.getConnection("jdbc:teradata://TOPDWPD", "NPCRMDW", "DO89CR33");
		return connection;
	}
	
	
	public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connection = null;
		}
	}
	
	
	

}
