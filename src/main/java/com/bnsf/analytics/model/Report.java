package com.bnsf.analytics.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

import com.bnsf.analytics.service.DataSourceService;

@Entity
@Table(name="DATA_SET_DEFF", schema = "NP_CRM1")
public class Report {
	
	@Transient
	@Autowired
	DataSourceService dsService;
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "REPORT_ID", nullable = false, unique = true)
	@Id
	private long reportId;
	
	private String name;
	
	//@Lob
	//@Column(length=100000)
	//private String query;
	
	@Lob
	private String query;
	
	
	private String label;
	
	@ManyToOne
	@JoinColumn(name="DATASOURCE_ID")
	private DataSource dataSource;
	
	
	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;
	
	
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String queryString) {
		this.query = queryString;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
