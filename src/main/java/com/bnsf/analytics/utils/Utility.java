package com.bnsf.analytics.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bnsf.analytics.model.SFDCDataSource;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Component
public class Utility {
	private static final Logger logger = LoggerFactory.getLogger(Utility.class);
	private static final String PROXY_URL="ftwrailproxy.bnsf.com";
	private static final int PROXY_PORT =8080;
	

	@Value("${spring.SFDC.url}")	
    private String sfdcUrl;	
	
	@Value("${spring.SFDC.username}")	 
    private String userName;	
	
	@Value("${spring.SFDC.password}")	
    private String password;
	
	
	public PartnerConnection getConnection(SFDCDataSource dataSource) {
		this.sfdcUrl = dataSource.getUrl();
		this.userName = dataSource.getUsername();
		this.password = dataSource.getPassword();
		
		return getConnection(dataSource.getUrl(),dataSource.getUsername(),dataSource.getPassword());
	}
	
	public PartnerConnection  getConnection()  {
		return  getConnection(this.sfdcUrl,this.userName,this.password); 
	}
	
	private PartnerConnection getConnection(String url, String userName, String password) {
		logger.info("Start : Utility.getConntection", sfdcUrl, userName); 
		PartnerConnection  connection = null;
		try {
			//System.out.println("sfdcUrl==========>"+ sfdcUrl);
			//System.out.println("userName==========>"+ userName);
			//System.out.println("password==========>"+ password);
			ConnectorConfig config = new ConnectorConfig();
			config.setUsername(userName);
			config.setPassword(password);
			config.setAuthEndpoint(sfdcUrl);
			config.setProxy(PROXY_URL, PROXY_PORT);
			connection = new PartnerConnection (config); 
		} catch (ConnectionException ce) {
			System.out.println("sfdcUrl==========>"+ sfdcUrl);
			System.out.println("userName==========>"+ userName);
			System.out.println("password==========>"+ password);
			ce.printStackTrace();
			logger.error("Utility.getConntection", ce.getMessage());
		}
		logger.info("End : Utility.getConntection", sfdcUrl, userName);
		return  connection; 
	}
	
	public boolean isConnectionValid(PartnerConnection connection) {
		logger.info("Start : Utility.getConntection");
		boolean connectionValid = true;
		try {
			GetUserInfoResult resut =connection.getUserInfo();
		} catch(ConnectionException ex) {
			logger.error("Utility.isConnectionValid", ex.getMessage());
			connectionValid = false;
		}
		logger.info("End : Utility.getConntection");
		return connectionValid;
	}

}
