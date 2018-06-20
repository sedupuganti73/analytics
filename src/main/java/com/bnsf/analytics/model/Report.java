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
	
	@Lob
	@Column(name = "Record_CNT_Query", nullable = true)
	private String recordCountQuery;
	
	@Column(name = "Load_Type", nullable = false, columnDefinition = "int default 0")
	private int loadType;
	
	@Column(name = "Run_Time", nullable = false,columnDefinition = "int default 0")
	private int runTime =0;
	
	@Column(name = "Priority", nullable = false, columnDefinition = "int default 0")
	private int priority;
	
	
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
	/**
	 * @return the loadType
	 */
	public int getLoadType() {
		return loadType;
	}
	/**
	 * @param loadType the loadType to set
	 */
	public void setLoadType(int loadType) {
		this.loadType = loadType;
	}
	/**
	 * @return the time
	 */
	public int getRunTime() {
		return runTime;
	}
	/**
	 * @param time the time to set
	 */
	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	/**
	 * @return the recordCountQuery
	 */
	public String getRecordCountQuery() {
		return recordCountQuery;
	}
	/**
	 * @param recordCountQuery the recordCountQuery to set
	 */
	public void setRecordCountQuery(String recordCountQuery) {
		this.recordCountQuery = recordCountQuery;
	}

}
