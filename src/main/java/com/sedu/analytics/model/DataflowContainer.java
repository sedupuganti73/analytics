package com.bnsf.analytics.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({
"dataflowJobs",
"url"
})
public class DataflowContainer {
	@JsonProperty("dataflowJobs")
	private List<DataflowJob> dataflowJobs = null;
	@JsonProperty("url")
	private String url;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("dataflowJobs")
	public List<DataflowJob> getDataflowJobs() {
	return dataflowJobs;
	}

	@JsonProperty("dataflowJobs")
	public void setDataflowJobs(List<DataflowJob> dataflowJobs) {
	this.dataflowJobs = dataflowJobs;
	}

	@JsonProperty("url")
	public String getUrl() {
	return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
	this.url = url;
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

