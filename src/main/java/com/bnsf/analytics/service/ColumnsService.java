package com.bnsf.analytics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnsf.analytics.model.ReportColumn;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.repositories.ColumnRepository;

import groovy.util.logging.Log4j;

@Log4j
@Service
public class ColumnsService {
	@Autowired
	private ColumnRepository columnRepository;
	
	public List<ReportColumn> getColumns(long reportId)  {
		return columnRepository.findByReportId(reportId);
	}
	
	public ReportColumn getColumn(long columnId)  {
		return columnRepository.findOne(columnId);
	}
	
	public void addColumn(ReportColumn column) {
		columnRepository.save(column);
	}
	
	public void deleteColumn (Long columnId) {
		columnRepository.delete(columnId);
	}
}
