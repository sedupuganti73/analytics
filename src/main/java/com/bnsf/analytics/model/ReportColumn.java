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
	private int scale;
	private int precision;
	
	@Column(name = "PRIMARY_KEY",  nullable = false, columnDefinition = "int default 0")
	private int primaryKey;
	
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
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
	/**
	 * @return the primaryKey
	 */
	public int getPrimaryKey() {
		return primaryKey;
	}
	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(int primaryKey) {
		this.primaryKey = primaryKey;
	}
	@Override
	public boolean equals(Object arg0) {
		System.out.println("in equals");
		// TODO Auto-generated method stub
		
		ReportColumn other = (ReportColumn)arg0;
		System.out.println("2nd arg "+other.name);
		System.out.println("1st arg "+this.name);
		return other.name.equals(this.name);
	}
	
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.name.hashCode();
	}
}
