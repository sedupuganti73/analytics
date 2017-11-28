package com.bnsf.analytics.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="DATA_SET_DEFF", schema = "NP_CRM1")
public class Report {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "REPORT_ID", nullable = false)
	@Id
	private long reportId;
	
	private String name;
	
	@Column(name = "DB_USER", nullable = false)
	private String dbUsername;
	
	@Column(name = "DB_PASSWD", nullable = false)
	private String dbPassword;
	
	@Lob
	@Column(length=100000)
	private byte[] query;
	
	@Transient
	private String queryString;
	
	
	private String label;
	
	@Column(name = "DB_TYPE", nullable = false)
	private Character type;
	
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
	public String getDbUsername() {
		return dbUsername;
	}
	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public byte[] getQuery() {
		return query;
	}
	public void setQuery(byte[] query) {
		this.query = query;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Character getType() {
		return type;
	}
	public void setType(Character type) {
		this.type = type;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.query = queryString.getBytes();
		this.queryString = queryString;
	}
}
