/**
 * 
 */
package com.bnsf.analytics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.model.SFDCDataSource;
import com.bnsf.analytics.service.ForceConnectionService;
import com.bnsf.analytics.utils.Utility;
import com.sforce.soap.partner.PartnerConnection;

import groovy.util.logging.Log4j;

/**
 * @author b031526
 *
 */
@Log4j
@RestController
@RequestMapping( "/api/forceDs")
public class SFDCController {
	
	@Autowired
	private ForceConnectionService forceService;
	
	
	@Autowired
	private Utility utility;
	
	@RequestMapping( "/")
    public List<SFDCDataSource> getDataSources() {
 		return forceService.getDataSources();
    }
	
	@RequestMapping( "/{dsId}")
    public SFDCDataSource getDataSource(
    			@PathVariable("dsId") Long dataSourceId
    		) {
 		return forceService.getDataSource(dataSourceId);
    }
 	
 	
 	@RequestMapping( value = "/", method = RequestMethod.POST)
    public SFDCDataSource addReport(
    			@RequestBody SFDCDataSource dataSource
    		) throws Exception{
 		PartnerConnection  partnerConnection = null;
 		partnerConnection = utility.getConnection(dataSource);
		if (!utility.isConnectionValid(partnerConnection)) {
			throw new Exception("Connection was not established. Please contact DB Administrator!!.");
		}
		forceService.addDataSource(dataSource);
 		 
 		
 		return dataSource;
    }
 	
 	@RequestMapping( value = "/{dsId}", method = RequestMethod.DELETE)
    public List<SFDCDataSource> deleteReport(
    			@PathVariable("dsId") Long dataSourceId
    		) {
 		forceService.deleteDataSource(dataSourceId);
 		return forceService.getDataSources();
    }

}
