package com.bnsf.analytics.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="DATA_SET_DATASOURCE", schema = "NP_CRM1")
public class DataSource {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "DATASOURCE_ID", nullable = false)
	@Id
	private long dsId;
	private String name;	
	private String description;
	
	@Column(name = "CONNECTION_URL", nullable = false)
	private String url;
	
	@Column(name = "DB_USER")
	private String dbUsername;
	
	
	@Column(name = "DB_PASSWORD")
	private String dbPassword;
	
	//@OneToMany(mappedBy = "dataSource", fetch = FetchType.LAZY)
	//private List<Report> reports;

	public long getDsId() {
		return dsId;
	}

	public void setDsId(long dsId) {
		this.dsId = dsId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	
}
