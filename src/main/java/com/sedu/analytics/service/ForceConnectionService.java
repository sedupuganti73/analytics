package com.bnsf.analytics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnsf.analytics.model.SFDCDataSource;
import com.bnsf.analytics.repositories.ForceConnectionRepository;

import groovy.util.logging.Log4j;

@Log4j
@Service
public class ForceConnectionService {
	
	@Autowired
	private ForceConnectionRepository fcRepository;
	
	public List<SFDCDataSource> getDataSources() {
		return fcRepository.findAll();
	}
	
	public SFDCDataSource getDataSource(long dsId) { 
		return fcRepository.findOne(dsId);
	}
	
	public void addDataSource(SFDCDataSource dataSource ) {
		fcRepository.save(dataSource);
	}
	
	public void deleteDataSource(Long dataSourceId) {
		fcRepository.delete(dataSourceId);
	}

}
