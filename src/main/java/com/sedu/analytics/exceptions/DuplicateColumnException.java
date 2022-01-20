package com.bnsf.analytics.exceptions;

public class DuplicateColumnException extends Exception{
	private String columnName ="";
	public DuplicateColumnException(String columnName){
		this.columnName = columnName;
	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return this.columnName+" is a duplicate column";
	}
}
