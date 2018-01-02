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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bnsf.analytics.dataObjects.FieldData;
import com.bnsf.analytics.dataObjects.FileFormat;
import com.bnsf.analytics.dataObjects.MetaData;
import com.bnsf.analytics.dataObjects.ObjectData;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.model.ReportColumn;
import com.sforce.soap.partner.Field;
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
	
	private static final String COMMA = "|";
	private static final String DOUBLE_QUOTE = "\"";
	private static final String UTF8_CHARACTERSET = "UTF-8";
	
	@Autowired
	private Utility utility;
	
	
	public void processLoad(String reportFolderPath, List<ReportColumn> reportColumnList, Report report) throws Exception {
		PartnerConnection connection = utility.getConnection();
		String metaDataJson = generateMetaDatatoLoad(report,reportColumnList);
		String dataSetId = createWaveDataSet(connection,metaDataJson,report);
		reportFolderPath = reportFolderPath+"\\" + report.getName()+".csv";
		pushFileDataToWave(dataSetId,reportFolderPath,connection);
		processData(dataSetId,connection);
	}
	
	
	private String createWaveDataSet(PartnerConnection connection,String metaDataJson, Report report ) throws Exception {
		String dataSetId = null;
		SObject sobj = new SObject();
        sobj.setType("InsightsExternalData");
        sobj.setField("Format","Csv");
        sobj.setField("EdgemartContainer", "SharedApp");
        sobj.setField("EdgemartAlias", report.getName());
        sobj.setField("MetadataJson",metaDataJson.getBytes());
        sobj.setField("Operation","Overwrite");
        sobj.setField("Action","None");
        SaveResult[] results = connection.create(new SObject[] { sobj });
        for(SaveResult sv:results) {
        	if(sv.isSuccess()) {
        		dataSetId = sv.getId();
        	} else {
        	    System.out.println("Error::"+sv.getErrors()[0].getFields());
        	    String[] errorArray = sv.getErrors()[0].getFields();
          	    throw new Exception(errorArray[0]);
        	}
        }
        return dataSetId;
	}
	
	
	private String generateMetaDatatoLoad(Report report,List<ReportColumn> reportColumnList) throws JsonGenerationException, JsonMappingException, IOException {
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
		String jsonStr = mapperObj.writeValueAsString(metadata);
		System.out.println("jsonStr----------->"+jsonStr);
		return jsonStr;
	}
	
	
	private List<FieldData> getFieldData(List<ReportColumn> reportColumnList,String dataName) {
		List<FieldData> fieldList = new ArrayList<FieldData>();
		for (ReportColumn reportColumn : reportColumnList) {
			if ('S' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Text"));
			} else if ('D' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Date", reportColumn.getFormat(), 0));
			} else if ('I' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Numeric", reportColumn.getFormat(), "0",reportColumn.getPrecision(), reportColumn.getScale()));
			} else if ('F' == reportColumn.getType()) {
				fieldList.add(new FieldData(reportColumn.getName(), reportColumn.getLabel(), dataName, "Numeric", reportColumn.getFormat(), "0",reportColumn.getPrecision(), reportColumn.getScale()));
			}
		}
		return fieldList;
	}
	
	
	private void pushFileDataToWave(String parentId, String filePath, PartnerConnection connection) throws ConnectionException,IOException {
		File currentFile = new File(filePath);
		double fileSize = getFileSize(currentFile);
		if (fileSize > 10) { 
			List<File> fileList = splitFile(currentFile,9);
			int count = 1;
			for (File tempFile : fileList) {
				writeFileData(parentId,count,tempFile,connection);
				count++;
			}
		} else {
			writeFileData(parentId,1,currentFile,connection);
		}
		 
         
	}
	private  double getFileSize(File file) {
	    double modifiedFileSize = 0.0;
	    double fileSize = 0.0;
	    if (file.isFile()) {
	        fileSize = (double) file.length();//in Bytes
             modifiedFileSize = Math.round((fileSize / (1024 * 1204) * 100.0)) / 100.0;
	    }

	    return modifiedFileSize;
	}
	
	private List<File> splitFile(File file, int sizeOfFileInMB) throws IOException {
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
	    	ex.printStackTrace();
	    } finally {
	    	if (br != null) {
	    		br.close();
	    	}
	    }
	    return files;
	}
	
	public String writeFileData(String parentId, int partNumber, File currentfile, PartnerConnection connection) throws ConnectionException {
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
        return rowId;
	}
	
	private  byte[] readBytesFromFile(File currentFile) {
		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;

		try {
			bytesArray = new byte[(int) currentFile.length()];

			// read file into bytes[]
			fileInputStream = new FileInputStream(currentFile);
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return bytesArray;
	}
	
	private String processData(String dataSetId, PartnerConnection connection) throws ConnectionException {
		String rowId= null;
		SObject sobj = new SObject();
		sobj.setType("InsightsExternalData");
		sobj.setField("Action","Process");
		sobj.setId(dataSetId); // This is the rowID from the previous example.
		SaveResult[] results = connection.update(new SObject[] { sobj });
		for(SaveResult sv:results) {
		     if(sv.isSuccess()) {
		         rowId = sv.getId();
		     }
		}
		return rowId;
	}
	
}
