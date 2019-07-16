/**
 * 
 */
package com.bnsf.analytics.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bnsf.analytics.dataObjects.FieldData;
import com.bnsf.analytics.dataObjects.FileFormat;
import com.bnsf.analytics.dataObjects.MetaData;
import com.bnsf.analytics.dataObjects.ObjectData;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.model.ReportColumn;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author B031526
 *
 */

@Component
public class LoadToCloud {
	private static final Logger logger = LoggerFactory.getLogger(LoadToCloud.class);
	private static final String COMMA = "|";
	private static final String DOUBLE_QUOTE = "\"";
	private static final String UTF8_CHARACTERSET = "UTF-8";
	private static final String INSERT ="Insert";
	private static final String UPDATE ="Update";
	
	@Autowired
	private Utility utility;
	
	public PartnerConnection getConnection() {
		return utility.getConnection();
	}
	
	/*
	public void processLoad(String reportFolderPath, List<ReportColumn> reportColumnList, Report report) throws Exception {
		logger.info("Start : LoadToCloud.processLoad");
		PartnerConnection connection = utility.getConnection();
		String metaDataJson = generateMetaDatatoLoad(report,reportColumnList);
		String dataSetId = createWaveDataSet(connection,metaDataJson,report);
		reportFolderPath = reportFolderPath+"\\" + report.getName()+".csv";
		pushFileDataToWave(dataSetId,reportFolderPath,connection);
		processData(dataSetId,connection);
		logger.info("End : LoadToCloud.processLoad");
	}
	*/
	
	public String createDataSetDefintion(PartnerConnection connection, Report report,List<ReportColumn> reportColumnList, String operation) throws Exception {
		logger.info("Start : LoadToCloud.createDataSetDefintion");
		String metaDataJson = generateMetaDatatoLoad(report,reportColumnList);
		String dataSetId = createWaveDataSet(connection,metaDataJson,report,operation);
		logger.info("End : LoadToCloud.createDataSetDefintion");
		return dataSetId;
	}
	
	
	private String createWaveDataSet(PartnerConnection connection,String metaDataJson, Report report , String operation) throws Exception {
		logger.info("Start : LoadToCloud.createWaveDataSet");
		String dataSetId = null;
		SObject sobj = new SObject();
        sobj.setType("InsightsExternalData");
        sobj.setField("Format","Csv");
        if (report.getAppName() != null ) {
            sobj.setField("EdgemartContainer", report.getAppName());
        } else {
        	 sobj.setField("EdgemartContainer", "SharedApp");
        }
        sobj.setField("EdgemartAlias", report.getName());
        sobj.setField("MetadataJson",metaDataJson.getBytes());
        sobj.setField("Operation",operation);
        sobj.setField("Action","None");
        SaveResult[] results = saveAndRetry(connection,new SObject[] { sobj });
        for(SaveResult sv:results) {
        	if(sv.isSuccess()) {
        		dataSetId = sv.getId();
        	} else {
        		logger.error("LoadToCloud.createWaveDataSet", sv.getErrors()[0].getFields());
        	    System.out.println("Error::"+sv.getErrors()[0].getFields());
        	    String[] errorArray = sv.getErrors()[0].getFields();
          	    throw new Exception(errorArray[0]);
        	}
        }
        logger.info("End : LoadToCloud.createWaveDataSet");
        return dataSetId;
	}
	
	
	private String generateMetaDatatoLoad(Report report,List<ReportColumn> reportColumnList) throws JsonGenerationException, JsonMappingException, IOException {
		logger.info("Start : LoadToCloud.generateMetaDatatoLoad");
		MetaData metadata = new MetaData();
		FileFormat format = new FileFormat();
		format.setCharsetName(UTF8_CHARACTERSET);
		format.setFieldsEnclosedBy(DOUBLE_QUOTE);
		format.setFieldsDelimitedBy(COMMA);
		format.setNumberOfLinesToIgnore(1);
		metadata.setFileFormat(format);
		List<ObjectData> objectList = new ArrayList<ObjectData>();
		ObjectData objectData = new ObjectData();
		objectData.setConnector("CSVConnector");
		objectData.setFullyQualifiedName(report.getName());
		if (report.getLabel() != null) {
		    objectData.setLabel(report.getLabel());
		} else {
			 objectData.setLabel(report.getName());
		}
		objectData.setName(report.getName());
		objectList.add(objectData);
		metadata.setObjects(objectList);
		objectData.setFields(getFieldData(reportColumnList,report.getName()));
		ObjectMapper mapperObj = new ObjectMapper();
		mapperObj.configure(Feature.WRITE_NULL_PROPERTIES, false);
		mapperObj.configure(Feature.INDENT_OUTPUT, true);
		String jsonStr = mapperObj.writeValueAsString(metadata);
		System.out.println("jsonStr----------->"+jsonStr);
		logger.info("End : LoadToCloud.generateMetaDatatoLoad");
		return jsonStr;
	}
	
	
	private List<FieldData> getFieldData(List<ReportColumn> reportColumnList,String dataName) {
		logger.info("Start : LoadToCloud.getFieldData");
		List<FieldData> fieldList = new ArrayList<FieldData>();
		boolean isUnique = false;
		for (ReportColumn reportColumn : reportColumnList) {
			isUnique = false;
			if (reportColumn.getPrimaryKey() == 1) {
				isUnique = true;
			}
			if ('S' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Text",isUnique));
			} else if ('D' == reportColumn.getType() || 'A' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Date", reportColumn.getFormat(), 0,isUnique));
			} else if ('I' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Numeric", reportColumn.getFormat(), "0",reportColumn.getPrecision(), reportColumn.getScale(),isUnique));
			} else if ('F' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Numeric", reportColumn.getFormat(), "0",reportColumn.getPrecision(), reportColumn.getScale(),isUnique));
			} else if ('T' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Numeric", reportColumn.getFormat(), "0",reportColumn.getPrecision(), reportColumn.getScale(),isUnique));
			}
		}
		logger.info("End : LoadToCloud.getFieldData");
		return fieldList;
	}
	
	
	private void pushFileDataToWave(String parentId, String filePath, PartnerConnection connection) throws ConnectionException,IOException {
		logger.info("Start : LoadToCloud.pushFileDataToWave");
		File currentFile = new File(filePath);
		double fileSize = getFileSize(currentFile);
		if (fileSize > 10) { 
			List<File> fileList = splitFile(currentFile,9);
			int count = 1;
			for (File tempFile : fileList) {
				writeFileData(parentId,count,tempFile,connection);
				//System.out.println("Processing File Number ::"+ count + " File Name ::"+ tempFile.getName());
				count++;
			}
		} else {
			writeFileData(parentId,1,currentFile,connection);
		}
		logger.info("End : LoadToCloud.pushFileDataToWave"); 
         
	}
	private  double getFileSize(File file) {
		logger.info("Start : LoadToCloud.getFileSize");
	    double modifiedFileSize = 0.0;
	    double fileSize = 0.0;
	    if (file.isFile()) {
	        fileSize = (double) file.length();//in Bytes
             modifiedFileSize = Math.round((fileSize / (1024 * 1204) * 100.0)) / 100.0;
	    }
	    logger.info("End : LoadToCloud.getFileSize");
	    return modifiedFileSize;
	}
	
	private List<File> splitFile(File file, int sizeOfFileInMB) throws IOException {
		logger.info("Start : LoadToCloud.splitFile");
	    int counter = 1;
	    List<File> files = new ArrayList<File>();
	    int sizeOfChunk = 1024 * 1024 * sizeOfFileInMB;
	    String eof = System.lineSeparator();
	    BufferedReader br = null;
	    OutputStream out = null;
	    File newFile = null;
	    
	    try {
	    	br = new BufferedReader(new FileReader(file));
	    	String name = file.getName();
	        String line = br.readLine();
	        while (line != null) {
	            newFile = new File(file.getParent(), name + "."+ String.format("%03d", counter++));
	            out = new BufferedOutputStream(new FileOutputStream(newFile));
	            int fileSize = 0;
                while (line != null) {
                    byte[] bytes = (line + eof).getBytes(Charset.defaultCharset());
                    if (fileSize + bytes.length > sizeOfChunk)
                        break;
                    out.write(bytes);
                    fileSize += bytes.length;
                    line = br.readLine();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
                files.add(newFile);
	        }
	    	
	    } catch(Exception ex) {
	    	logger.error("LoadToCloud.createWaveDataSet", ex.getMessage());
	    } finally {
	    	if (br != null) {
	    		br.close();
	    	}
	    }
	    logger.info("End : LoadToCloud.splitFile");
	    return files;
	}
	
	public String publishDataToWave(PartnerConnection connection,byte[] byteArray,String parentId, int partNumber) throws Exception {
		logger.info("Start : LoadToCloud.publishDataToWave");
		/*if (!utility.isConnectionValid(connection)) {
        	connection = utility.getConnection();
        }*/
		String rowId= null;
		try {
			
			SObject sobj = new SObject();
	        sobj.setType("InsightsExternalDataPart"); 
	        sobj.setField("DataFile",byteArray);
	        sobj.setField("InsightsExternalDataId", parentId);
	        sobj.setField("PartNumber",partNumber); //Part numbers should start at 1
	        SaveResult[] results = saveAndRetry(connection,(new SObject[] { sobj }));
	        for(SaveResult sv:results) {
	            if(sv.isSuccess()) {
	           	    rowId = sv.getId();
	            } else {
	            	logger.error("LoadToCloud.publishDataToWave", sv.getErrors()[0].getFields());
	        	    System.out.println("Error::"+sv.getErrors()[0].getFields());
	        	    String[] errorArray = sv.getErrors()[0].getFields();
	        	    System.out.println("**************** "+ sv.getErrors()[0] +"*******************");
	            }
	        }
		} catch (Exception ex) {
			logger.error("LoadToCloud.publishDataToWave", ex.getMessage());
			logger.error("LoadToCloud.publishDataToWave", ex.getStackTrace());
			throw new Exception(ex.getMessage());
		}
        logger.info("End : LoadToCloud.publishDataToWave");
        return rowId;
	}
	
	private  SaveResult[]  saveAndRetry(PartnerConnection connection, SObject[] objArray) throws Exception {
		return saveAndRetry(connection,objArray,INSERT);
	}
	 
	private SaveResult[]  saveAndRetry(PartnerConnection connection, SObject[] objArray,String operation) throws Exception {
		logger.info("Start : LoadToCloud.saveAndRetry");
		SaveResult[] results = null;
		for (int i=0; i< 4; i++ ) {
			try {
				if (operation.equalsIgnoreCase(INSERT)) {
				    results = connection.create(objArray);
				} else {
					results = connection.update(objArray);
				}
		        break;
			} catch (ConnectionException ex) {
				ex.printStackTrace();
				logger.error("ReportData.extractData", ex.getMessage());
				continue;
			}
		}
		logger.info("End : LoadToCloud.saveAndRetry");
		return results;
	}
	
	public String writeFileData(String parentId, int partNumber, File currentfile, PartnerConnection connection) throws ConnectionException {
		logger.info("Start : LoadToCloud.writeFileData");
		if (!utility.isConnectionValid(connection)) {
        	connection = utility.getConnection();
        }
		String rowId= null;
		SObject sobj = new SObject();
        sobj.setType("InsightsExternalDataPart"); 
        sobj.setField("DataFile",readBytesFromFile(currentfile));
        sobj.setField("InsightsExternalDataId", parentId);
        sobj.setField("PartNumber",partNumber); //Part numbers should start at 1
        SaveResult[] results = connection.create(new SObject[] { sobj });
        for(SaveResult sv:results) {
            if(sv.isSuccess()) {
           	 rowId = sv.getId();
            }
        }
        logger.info("End : LoadToCloud.writeFileData");
        return rowId;
	}
	
	private  byte[] readBytesFromFile(File currentFile) {
		logger.info("Start : LoadToCloud.readBytesFromFile");
		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;

		try {
			bytesArray = new byte[(int) currentFile.length()];
			fileInputStream = new FileInputStream(currentFile);
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			logger.error("LoadToCloud.readBytesFromFile", e.getMessage());
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					logger.error("LoadToCloud.readBytesFromFile", e.getMessage());

				}
			}

		}
		logger.info("End : LoadToCloud.readBytesFromFile");
		return bytesArray;
	}
	
	public String processData(String dataSetId, PartnerConnection connection) throws Exception {
		logger.info("Start : LoadToCloud.processData");
		/*if (!utility.isConnectionValid(connection)) {
        	connection = utility.getConnection();
        }*/
		String rowId= null;
		SObject sobj = new SObject();
		sobj.setType("InsightsExternalData");
		sobj.setField("Action","Process");
		sobj.setId(dataSetId); // This is the rowID from the previous example.
		SaveResult[] results =saveAndRetry(connection, new SObject[] { sobj },UPDATE);
		for(SaveResult sv:results) {
		     if(sv.isSuccess()) {
		         rowId = sv.getId();
		     }
		}
		logger.info("End : LoadToCloud.processData");
		return rowId;
	}
	
}
