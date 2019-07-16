package com.bnsf.analytics.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;

import com.bnsf.analytics.service.DataSourceService;
import com.bnsf.analytics.service.ForceConnectionService;

@Entity
@Table(name="DATA_SET_DEFF", schema = "NP_CRM1")
public class Report {
	
	@Transient
	@Autowired
	DataSourceService dsService;
	
	@Transient
	@Autowired
	ForceConnectionService forceConnectionService;
	
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
	
	@Column(name = "Method", nullable = true)
	private String method;
	
	
	@Column(name = "is_incremental", nullable = false,columnDefinition = "int default 0")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isIncremental;
	
	@Column(name = "incremental_value", nullable = true)
	private String incrementalValue;
	
	
	private String label;
	
	@ManyToOne
	@JoinColumn(name="DATASOURCE_ID")
	private DataSource dataSource;
	
	
	@ManyToOne
    @JoinColumn(columnDefinition="integer",name="SFDC_DATASOURCE_ID", referencedColumnName="DATASOURCE_ID")
	private SFDCDataSource sfdcDataSource;
	
		
	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;
	
	
	@Column(name = "AppName", nullable = true)
	private String appName;
	
	
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
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the sfdcDataSource
	 */
	public SFDCDataSource getSfdcDataSource() {
		return sfdcDataSource;
	}
	/**
	 * @param sfdcDataSource the sfdcDataSource to set
	 */
	public void setSfdcDataSource(SFDCDataSource sfdcDataSource) {
		this.sfdcDataSource = sfdcDataSource;
	}
	/**
	 * @return the isIncremental
	 */
	public boolean isIncremental() {
		return isIncremental;
	}
	/**
	 * @param isIncremental the isIncremental to set
	 */
	public void setIncremental(boolean isIncremental) {
		this.isIncremental = isIncremental;
	}
	/**
	 * @return the incrementalValue
	 */
	public String getIncrementalValue() {
		return incrementalValue;
	}
	/**
	 * @param incrementalValue the incrementalValue to set
	 */
	public void setIncrementalValue(String incrementalValue) {
		this.incrementalValue = incrementalValue;
	}
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	
}
