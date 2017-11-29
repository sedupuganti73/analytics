package com.bnsf.analytics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnsf.analytics.model.DataSource;
import com.bnsf.analytics.repositories.DSRepository;

import groovy.util.logging.Log4j;

@Log4j
@Service
public class DataSourceService {

	@Autowired
	private DSRepository dsRepository;
	
	public List<DataSource> getDataSources() {
		return dsRepository.findAll();
	}
	
	public void addDataSource(DataSource dataSource ) {
		dsRepository.save(dataSource);
	}
	
	public void deleteDataSource(Long dataSourceId) {
		dsRepository.delete(dataSourceId);
	}
}
