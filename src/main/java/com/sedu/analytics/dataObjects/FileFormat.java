package com.bnsf.analytics.dataObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class FileFormat {
	
	private String charsetName;
	private String fieldsEnclosedBy;
	private String fieldsDelimitedBy;
	private Integer numberOfLinesToIgnore;
	/**
	 * @return the charsetName
	 */
	public String getCharsetName() {
		return charsetName;
	}
	/**
	 * @param charsetName the charsetName to set
	 */
	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}
	/**
	 * @return the fieldsEnclosedBy
	 */
	public String getFieldsEnclosedBy() {
		return fieldsEnclosedBy;
	}
	/**
	 * @param fieldsEnclosedBy the fieldsEnclosedBy to set
	 */
	public void setFieldsEnclosedBy(String fieldsEnclosedBy) {
		this.fieldsEnclosedBy = fieldsEnclosedBy;
	}
	/**
	 * @return the fieldsDelimitedBy
	 */
	public String getFieldsDelimitedBy() {
		return fieldsDelimitedBy;
	}
	/**
	 * @param fieldsDelimitedBy the fieldsDelimitedBy to set
	 */
	public void setFieldsDelimitedBy(String fieldsDelimitedBy) {
		this.fieldsDelimitedBy = fieldsDelimitedBy;
	}
	/**
	 * @return the numberOfLinesToIgnore
	 */
	public Integer getNumberOfLinesToIgnore() {
		return numberOfLinesToIgnore;
	}
	/**
	 * @param numberOfLinesToIgnore the numberOfLinesToIgnore to set
	 */
	public void setNumberOfLinesToIgnore(Integer numberOfLinesToIgnore) {
		this.numberOfLinesToIgnore = numberOfLinesToIgnore;
	}

}
