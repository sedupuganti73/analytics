package com.bnsf.analytics.service;

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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnsf.analytics.dataObjects.FieldData;
import com.bnsf.analytics.dataObjects.FileFormat;
import com.bnsf.analytics.dataObjects.MetaData;
import com.bnsf.analytics.dataObjects.ObjectData;
import com.bnsf.analytics.utils.Utility;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

@Service
public class DataLoadService {

	private static final String COMMA = ",";
	private static final String DOUBLE_QUOTE = "\"";
	private static final String UTF8_CHARACTERSET = "UTF-8";

	@Autowired
	private Utility utility;

	public static void main(String args[]) {
		DataLoadService service = new DataLoadService();
		service.loadData();
	}

	public void loadData()  {
				 
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
		objectData.setFullyQualifiedName("RevenueData");
		objectData.setLabel("Revenue Data");
		objectData.setName("RevenueData");
		objectList.add(objectData);
		metadata.setObjects(objectList);
		objectData.setFields(getFieldData("RevenueData"));
		
		
        ObjectMapper mapperObj = new ObjectMapper();
        //mapperObj.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL); 
        try {
            String jsonStr = mapperObj.writeValueAsString(metadata);
            boolean isValid =isJSONValid(jsonStr);
            System.out.println(jsonStr);
            System.out.println("isValid----------->"+ isValid);
            SObject sobj = new SObject();
            sobj.setType("InsightsExternalData");
            sobj.setField("Format","Csv");
            sobj.setField("EdgemartContainer", "SharedApp");
            sobj.setField("EdgemartAlias", "RevenueData");
            sobj.setField("MetadataJson",jsonStr.getBytes());
            sobj.setField("Operation","Overwrite");
            sobj.setField("Action","None");
            SaveResult[] results = utility.getConnection().create(new SObject[] { sobj });
            String parentId = null;
            for(SaveResult sv:results) {
            	if(sv.isSuccess()) {
            		parentId = sv.getId();
            	} else {
            	    System.out.println("Error::"+sv.getErrors()[0].getFields());
            	}
            }
            loadFileData(parentId,"C:\\Users\\B031526\\Downloads\\RevenueDataExport09062017.csv");
            processData(parentId); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();    
        } catch (ConnectionException ex) {
        	ex.printStackTrace();
        }
	}

	private void loadFileData(String parentId, String filePath) throws ConnectionException,IOException {
		File currentFile = new File(filePath);
		double fileSize = getFileSize(currentFile);
		if (fileSize > 10) { 
			List<File> fileList = splitFile(currentFile,9);
			int count = 1;
			for (File tempFile : fileList) {
				writeFileData(parentId,count,tempFile);
				count++;
			}
		} else {
			writeFileData(parentId,1,currentFile);
		}
		 
         
	}
		
	public String writeFileData(String parentId, int partNumber, File currentfile) throws ConnectionException {
		 String rowId= null;
		 SObject sobj = new SObject();
         sobj.setType("InsightsExternalDataPart");
         sobj.setField("DataFile",readBytesFromFile(currentfile));
         sobj.setField("InsightsExternalDataId", parentId);
         sobj.setField("PartNumber",partNumber); //Part numbers should start at 1
         SaveResult[] results = utility.getConnection().create(new SObject[] { sobj });
         for(SaveResult sv:results) {
             if(sv.isSuccess()) {
            	 rowId = sv.getId();
             }
         }
         return rowId;
	}
	
	
	public static List<File> splitFile(File file, int sizeOfFileInMB) throws IOException {
	    int counter = 1;
	    List<File> files = new ArrayList<File>();
	    int sizeOfChunk = 1024 * 1024 * sizeOfFileInMB;
	    String eof = System.lineSeparator();
	    
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(file));
	    	String name = file.getName();
	        String line = br.readLine();
	        while (line != null) {
	            File newFile = new File(file.getParent(), name + "."+ String.format("%03d", counter++));
	            OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
	            int fileSize = 0;
                while (line != null) {
                    byte[] bytes = (line + eof).getBytes(Charset.defaultCharset());
                    if (fileSize + bytes.length > sizeOfChunk)
                        break;
                    out.write(bytes);
                    fileSize += bytes.length;
                    line = br.readLine();
                }
                files.add(newFile);
	        }
	    	
	    } catch(Exception ex) {
	    	ex.printStackTrace();
	    }
	    return files;
	}
	
	private String processData(String parentId) throws ConnectionException {
		String rowId= null;
		SObject sobj = new SObject();
		sobj.setType("InsightsExternalData");
		sobj.setField("Action","Process");
		sobj.setId(parentId); // This is the rowID from the previous example.
		SaveResult[] results = utility.getConnection().update(new SObject[] { sobj });
		for(SaveResult sv:results) {
		     if(sv.isSuccess()) {
		         rowId = sv.getId();
		     }
		}
		return rowId;
	}
	private List<FieldData> getFieldData(String dataName) {
		List<FieldData> fieldList = new ArrayList<FieldData>();
		fieldList.add(new FieldData("CurrentDate", "Current Date", dataName, "Date", "MM/dd/yyyy", 0));
		fieldList.add(new FieldData("BSRLNAME", "BSRL Name", dataName, "Text"));
		fieldList.add(new FieldData("BSRLRole", "BSRL Role", dataName, "Text"));
		fieldList.add(new FieldData("SUBFCSTCODE", "Sub Forecast Code", dataName, "Text"));
		fieldList.add(new FieldData("SUBFCST", "Sub Forecast", dataName, "Text"));
		fieldList.add(new FieldData("FCST", "Forecast", dataName, "Text"));
		fieldList.add(new FieldData("BUSSUNIT", "Bussiness Unit", dataName, "Text"));
		fieldList.add(new FieldData("BUSSGRP", "Bussiness Group", dataName, "Text"));
		fieldList.add(new FieldData("UNITS", "Units", dataName, "Numeric", "#,##0.00", "0", 5, 2));
		fieldList.add(new FieldData("NETREV", "Net Revenue", dataName, "Numeric", "$#,##0.00","0", 10, 2));
		fieldList.add(new FieldData("CONTR", "Contract", dataName, "Numeric", "#,##0.00", "0", 10, 2));
		fieldList.add(new FieldData("TONS", "Tons", dataName, "Numeric", "#,##0.00", "0", 10, 2));
		return fieldList;
	}

	private static byte[] readBytesFromFile(File currentFile) {
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
	
	public static boolean isJSONValid(String jsonInString ) {
		boolean isValid = false;
	    try {
	       final ObjectMapper mapper = new ObjectMapper();
	       mapper.readTree(jsonInString);
	       isValid =true;
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
	    
	    return isValid;
	  }
	
	public static double getFileSize(File file) {
	    double modifiedFileSize = 0.0;
	    double fileSize = 0.0;
	    if (file.isFile()) {
	        fileSize = (double) file.length();//in Bytes
             modifiedFileSize = Math.round((fileSize / (1024 * 1204) * 100.0)) / 100.0;
	    }

	    return modifiedFileSize;
	}

}
