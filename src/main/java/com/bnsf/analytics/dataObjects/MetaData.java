package com.bnsf.analytics.dataObjects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MetaData {
	
	
	/**
	 * @return the fileFormat
	 */
	public FileFormat getFileFormat() {
		return fileFormat;
	}
	/**
	 * @param fileFormat the fileFormat to set
	 */
	public void setFileFormat(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}
	/**
	 * @return the objects
	 */
	public List<ObjectData> getObjects() {
		return objects;
	}
	/**
	 * @param objects the objects to set
	 */
	public void setObjects(List<ObjectData> objects) {
		this.objects = objects;
	}
	private FileFormat fileFormat;
	private List<ObjectData> objects;

}
