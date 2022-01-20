package com.bnsf.analytics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.model.ReportColumn;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.service.ColumnsService;

import groovy.util.logging.Log4j;

@Log4j
@RestController
@RequestMapping( "/api/columns")
public class ColumnsController {
	@Autowired
	private ColumnsService columnsService;
	
	@RequestMapping( "/{reportId}")
    public List<ReportColumn> getColumns(@PathVariable("reportId") Long reportId) {
 		return columnsService.getColumns(reportId);
    }
	
	@RequestMapping( "/column/{columnId}")
    public ReportColumn getColumn(@PathVariable("columnId") Long columnId) {
 		return columnsService.getColumn(columnId);
    }
 	
 	@RequestMapping( value = "/create", method = RequestMethod.POST)
    public ReportColumn addColumn(
    			@RequestBody ReportColumn column
    		) {
 		columnsService.addColumn(column);
 		return column;
    }
 	
 	@RequestMapping( value = "/delete/{columnId}/{reportId}", method = RequestMethod.DELETE)
    public List<ReportColumn> deleteColumn(
    			@PathVariable("columnId") Long columnId ,
    			@PathVariable("reportId") Long reportId
    		) {
 		columnsService.deleteColumn(columnId);
 		return columnsService.getColumns(reportId);
    }
}
