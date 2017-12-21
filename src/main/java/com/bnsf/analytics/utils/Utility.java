package com.bnsf.analytics.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Component
public class Utility {
	
	private static final String PROXY_URL="ftwrailproxy.bnsf.com";
	private static final int PROXY_PORT =8080;
	

	@Value("${spring.SFDC.url}")	
    private String sfdcUrl;	
	
	@Value("${spring.SFDC.username}")	 
    private String userName;	
	
	@Value("${spring.SFDC.password}")	
    private String password;
	
	public PartnerConnection  getConnection()  {
		PartnerConnection  connection = null;
		try {
			System.out.println("sfdcUrl==========>"+ sfdcUrl);
			System.out.println("userName==========>"+ userName);
			System.out.println("password==========>"+ password);
			ConnectorConfig config = new ConnectorConfig();
			config.setUsername(userName);
			config.setPassword(password);
			config.setAuthEndpoint(sfdcUrl);
			config.setProxy(PROXY_URL, PROXY_PORT);
			connection = new PartnerConnection (config); 
		} catch (ConnectionException ce) {
			ce.printStackTrace();
			//throw new Exception(ce.getMessage());
		}
		return  connection; 
	}

}
