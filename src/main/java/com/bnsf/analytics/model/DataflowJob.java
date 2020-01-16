package com.bnsf.analytics.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;




@JsonPropertyOrder({ "createdDate", "dataflow", "duration", "id", "label", "startDate", "status", "type", "url",
		"message","jobType" })
public class DataflowJob {

	@JsonProperty("createdDate")
	private String createdDate;
	@JsonProperty("dataflow")
	private Dataflow dataflow;
	@JsonProperty("duration")
	private Integer duration;
	@JsonProperty("id")
	private String id;
	@JsonProperty("label")
	private String label;
	@JsonProperty("startDate")
	private String startDate;
	@JsonProperty("status")
	private String status;
	@JsonProperty("type")
	private String type;
	@JsonProperty("url")
	private String url;
	@JsonProperty("message")
	private String message;
	@JsonProperty("jobType")
	private String jobType;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("createdDate")
	public String getCreatedDate() {
		return createdDate;
	}

	@JsonProperty("createdDate")
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	@JsonProperty("dataflow")
	public Dataflow getDataflow() {
		return dataflow;
	}

	@JsonProperty("dataflow")
	public void setDataflow(Dataflow dataflow) {
		this.dataflow = dataflow;
	}

	@JsonProperty("duration")
	public Integer getDuration() {
		return duration;
	}

	@JsonProperty("duration")
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("label")
	public String getLabel() {
		return label;
	}

	@JsonProperty("label")
	public void setLabel(String label) {
		this.label = label;
	}

	@JsonProperty("startDate")
	public String getStartDate() {
		return startDate;
	}

	@JsonProperty("startDate")
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@JsonProperty("message")
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the jobType
	 */
	public String getJobType() {
		return jobType;
	}

	/**
	 * @param jobType the jobType to set
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
