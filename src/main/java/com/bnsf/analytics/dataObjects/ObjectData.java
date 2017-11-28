package com.bnsf.analytics.dataObjects;

import java.util.List;

public class ObjectData {
	
	private String connector;
	private String description;
	private String fullyQualifiedName;
	private String label;
	private String name;
	private List<FieldData> fields;
	
	/**
	 * @return the connector
	 */
	public String getConnector() {
		return connector;
	}
	/**
	 * @param connector the connector to set
	 */
	public void setConnector(String connector) {
		this.connector = connector;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the fullyQualifiedName
	 */
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}
	/**
	 * @param fullyQualifiedName the fullyQualifiedName to set
	 */
	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the fields
	 */
	public List<FieldData> getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<FieldData> fields) {
		this.fields = fields;
	}
	

}
