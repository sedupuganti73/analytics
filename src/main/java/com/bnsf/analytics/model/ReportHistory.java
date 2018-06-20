/**
 * 
 */
package com.bnsf.analytics.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

import com.bnsf.analytics.service.ReportsService;

/**
 * @author B031526
 *
 */

@Entity
@Table(name="REPORT_HISTORY", schema = "NP_CRM1")
public class ReportHistory {
	
	
	@Transient
	@Autowired
	ReportsService reportService;
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "HISTORY_ID", nullable = false, unique = true)
	@Id
	private long historyId;
	
	
	@Column(name = "START_DATE", nullable = false)
	private LocalDateTime startDate;
	
	
	@Column(name = "END_DATE", nullable = true)
	private LocalDateTime endDate;
	
	
	@Column(name = "MESSAGE", nullable = false)
	private String message;
	
	
	@Column(name ="REPORT_ID", nullable = false)
	private long reportId;
	
	@Column(name ="RECORD_CNT_BEFORE", nullable = false, columnDefinition = "int default 0")
	private long recordCountBefore;
	
	@Column(name ="RECORD_CNT_AFTER", nullable = false, columnDefinition = "int default 0")
	private long recordCountAfter;

	/**
	 * @return the historyId
	 */
	public long getHistoryId() {
		return historyId;
	}

	/**
	 * @param historyId the historyId to set
	 */
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}

	/**
	 * @return the startDate
	 */
	public LocalDateTime getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public LocalDateTime getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the report
	 */
	public Long getReportId() {
		return reportId;
	}

	/**
	 * @param report the report to set
	 */
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	/**
	 * @return the recordCountBefore
	 */
	public long getRecordCountBefore() {
		return recordCountBefore;
	}

	/**
	 * @param recordCountBefore the recordCountBefore to set
	 */
	public void setRecordCountBefore(long recordCountBefore) {
		this.recordCountBefore = recordCountBefore;
	}

	/**
	 * @return the recordCountAfter
	 */
	public long getRecordCountAfter() {
		return recordCountAfter;
	}

	/**
	 * @param recordCountAfter the recordCountAfter to set
	 */
	public void setRecordCountAfter(long recordCountAfter) {
		this.recordCountAfter = recordCountAfter;
	}
	

}
