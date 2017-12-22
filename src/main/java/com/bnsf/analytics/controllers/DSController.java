package com.bnsf.analytics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.model.DataSource;
import com.bnsf.analytics.service.DataSourceService;

import groovy.util.logging.Log4j;

@Log4j
@RestController
@RequestMapping( "/api/ds")
public class DSController {
	@Autowired
	private DataSourceService dsService;
	
	@RequestMapping( "/")
    public List<DataSource> getDataSources() {
 		return dsService.getDataSources();
    }
	
	@RequestMapping( "/{dsId}")
    public DataSource getDataSource(
    			@PathVariable("dsId") Long dataSourceId
    		) {
 		return dsService.getDataSource(dataSourceId);
    }
 	
 	
 	@RequestMapping( value = "/", method = RequestMethod.POST)
    public DataSource addReport(
    			@RequestBody DataSource dataSource
    		) {
 		dsService.addDataSource(dataSource);
 		return dataSource;
    }
 	
 	@RequestMapping( value = "/{dsId}", method = RequestMethod.DELETE)
    public List<DataSource> deleteReport(
    			@PathVariable("dsId") Long dataSourceId
    		) {
 		dsService.deleteDataSource(dataSourceId);
 		return dsService.getDataSources();
    }
}
