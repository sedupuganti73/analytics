package com.bnsf.analytics.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="DATA_SET_COL_DEFF", schema = "NP_CRM1")
public class ReportColumn {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COLUMN_ID", nullable = false)
	@Id
	private long columnId;
	
	private Character type;
	private String format;
	private String name;
	private String label;
	
	@Column(name = "REPORT_ID", nullable = false)
	private long reportId;
	
	
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public long getColumnId() {
		return columnId;
	}
	public void setColumnId(long columnId) {
		this.columnId = columnId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Character getType() {
		return type;
	}
	public void setType(Character type) {
		this.type = type;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
}
