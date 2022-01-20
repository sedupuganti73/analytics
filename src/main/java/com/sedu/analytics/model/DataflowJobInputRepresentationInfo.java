package com.bnsf.analytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
"command",
"dataflowId"
})
public class DataflowJobInputRepresentationInfo {

	@JsonProperty("command")
	private String command;
	
	@JsonProperty("dataflowId")
	private String dataflowId;

	/**
	 * @return the command
	 */
	@JsonProperty("command")
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	@JsonProperty("command")
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the dataflowId
	 */
	@JsonProperty("dataflowId")
	public String getDataflowId() {
		return dataflowId;
	}

	/**
	 * @param dataflowId the dataflowId to set
	 */
	@JsonProperty("dataflowId")
	public void setDataflowId(String dataflowId) {
		this.dataflowId = dataflowId;
	}
}
