package com.bnsf.analytics.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.bnsf.analytics.convertors.CryptoConvertor;

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
	
	@Convert(converter= CryptoConvertor.class)
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
