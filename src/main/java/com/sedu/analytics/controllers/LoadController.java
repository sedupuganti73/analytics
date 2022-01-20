package com.bnsf.analytics.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bnsf.analytics.model.DataflowContainer;
import com.bnsf.analytics.model.DataflowJob;
import com.bnsf.analytics.model.DataflowJobInputRepresentationInfo;
import com.bnsf.analytics.model.Report;
import com.bnsf.analytics.service.DataLoadService;
import com.bnsf.analytics.service.ReportsService;
import com.bnsf.analytics.utils.HttpUtils;
import com.bnsf.analytics.utils.Utility;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sforce.soap.partner.PartnerConnection;

import groovy.util.logging.Log4j;

@Log4j
@RestController
@RequestMapping( "/api/load")
public class LoadController {
	private static final Logger logger = LoggerFactory.getLogger(LoadController.class);
	private static final String COMMA_STR =",";
	private static final String SUCCESS ="Success"; 
	private static final String WARNING ="Warning";
	private static final String FAILURE ="Failure";
	private static final String FILE_UPLOAD_JOBTYPE="fileupload";
	private static final String USER_JOBTYPE="USER";
	
	@Autowired
	private DataLoadService dataLoadService;
	
	@Autowired
	private ReportsService reportService;
	
	@Autowired
	private HttpUtils httpUitls;
	
	
	@Autowired
	private Utility utility;
	
	public PartnerConnection getConnection() {
		return utility.getConnection();
	}
	
	@RequestMapping( "/")
    public void runAllReports() {
 		 dataLoadService.loadData();
    }
	
	@RequestMapping("/{reportId}") 
	public void runReport(@PathVariable("reportId") Long reportId) {
		dataLoadService.loadData(reportService.getReport(reportId));
	}
	
	
	@PostMapping(path = "/")
	public int runReport(@RequestBody String reportNames) {
		logger.info("Start : LoadController.runReport");
		System.out.println("reportNames:::"+reportNames);
		int status = 200;
    	if (reportNames != null && reportNames.trim().length() > 0) {
			StringTokenizer str = new StringTokenizer(reportNames,COMMA_STR);
			String reportName = null;
			 while (str.hasMoreTokens()) {
				 reportName = str.nextToken();
				 if ("BSRL".equalsIgnoreCase(reportName)) {
					 reportName = "BSRL2";
				 }
				 if (reportName != null && reportName.trim().length() > 0) {
					 Report currentReport =reportService.getReport(reportName);
					 //System.out.println("currentReport Name:::"+currentReport.getName());
					 if (currentReport != null) {
						 PartnerConnection partnerConnection =dataLoadService.loadData(currentReport);
						 if (currentReport.getFlowName() != null && currentReport.getFlowName().trim().length() > 0) {
							 CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
								    return checkAndExecuteFlow(currentReport,partnerConnection);
								});
						 }
					 } 
				 }
			 }
			 /*
			 System.out.println("Done Loading  Name:::"+currentReport.getName());
			 if (currentReport != null && currentReport.getName().equalsIgnoreCase("Pace")) {
				 PartnerConnection partnerConnection = null;
					try {
						partnerConnection = utility.getConnection(currentReport.getSfdcDataSource());
						System.out.println("Connection:::"+partnerConnection);
						if (partnerConnection != null && partnerConnection.getConfig() != null && partnerConnection.getConfig().getSessionId() != null) {
							String sessionId =partnerConnection.getConfig().getSessionId();
							String endPointUrl =getServiceEndPointUrl(partnerConnection.getConfig().getServiceEndpoint());
							 Map<String,DataflowJob> flowMap = getDataFlowJobs(sessionId,endPointUrl);
							 
							 if (flowMap != null) {
								 DataflowJob flowJob =flowMap.get("Pace");
								 System.out.println("Pace Flow:::"+flowJob.getLabel());
								 int successCode = isDataLoadComplete(flowJob,endPointUrl,sessionId);
								 System.out.println("Loading Success Code:::"+successCode);
								 if (successCode == 0) {
									 DataflowJob marketingFlowJob = flowMap.get("MarketingFlow");
									 executeDataFlow(marketingFlowJob,endPointUrl,sessionId);
								 }
							 }
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("DataLoadService.loadData(report)", e.getMessage());
						status = 404;
					} finally {
						partnerConnection = null;
					}
			 }*/
			
		}
		
		logger.info("End : LoadController.runReport"); 
		return status;
	}
	
	private String checkAndExecuteFlow( Report currentReport,PartnerConnection partnerConnection)  {
		int status = 200;
		String sessionId =partnerConnection.getConfig().getSessionId();
		String endPointUrl =getServiceEndPointUrl(partnerConnection.getConfig().getServiceEndpoint());
		try {
			Map<String,DataflowJob> flowMap = getReportDataFlowJobs(sessionId,endPointUrl,currentReport);
			if (flowMap != null) {
				 DataflowJob flowJob =flowMap.get(currentReport.getName());
				 System.out.println(" Flow Name:::"+flowJob.getLabel());
				 int successCode = isDataLoadComplete(flowJob,endPointUrl,sessionId);
				 System.out.println("Loading Success Code:::"+successCode);
				 if (successCode == 0) {
					 DataflowJob marketingFlowJob = flowMap.get(currentReport.getFlowName());
					 executeDataFlow(marketingFlowJob,endPointUrl,sessionId);
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DataLoadService.loadData(report)", e.getMessage());
			status = 404;
		} 
		return String.valueOf(status);
	}
	
	private void executeDataFlow(DataflowJob marketingFlowJob,String endPointUrl,String sessionId) throws JsonGenerationException, JsonMappingException, IOException, ParseException, InterruptedException, java.text.ParseException {
		String jobsUrl = endPointUrl+"data/v42.0/wave/dataflowjobs";
		ObjectMapper mapper = new ObjectMapper();
		DataflowJobInputRepresentationInfo dataflowJob = new DataflowJobInputRepresentationInfo();
		 dataflowJob.setCommand("Start");
		 dataflowJob.setDataflowId(marketingFlowJob.getDataflow().getId());
		 String dataflowJson = mapper.writeValueAsString(dataflowJob);
		 String response = httpUitls.patchData(jobsUrl,sessionId,dataflowJson,false);
		 System.out.println("Rsponse:::"+response);
	}
	
	private int isDataLoadComplete(DataflowJob flowJob,String endPointUrl,String sessionId) throws ClientProtocolException, IOException, InterruptedException {
		int successCode = -1;
		String jobsUrl = endPointUrl+"data/v42.0/wave/dataflowjobs/"+flowJob.getId();
		ObjectMapper mapper = new ObjectMapper();
		DataflowJob job = null;
		for(int i=0 ; i < 10; i++) {
			Thread.sleep(60*1000*5);
			String data =httpUitls.getData(jobsUrl,sessionId);
			job =mapper.readValue(data, DataflowJob.class); 
			if (SUCCESS.equalsIgnoreCase(job.getStatus())||WARNING.equalsIgnoreCase(job.getStatus())) {
				successCode = 0;
				break;
			} else if (FAILURE.equalsIgnoreCase(job.getStatus())) {
				successCode = -1;
				break;
			}
		}
		
		return successCode;
	}
	
	private Map<String,DataflowJob> getReportDataFlowJobs(String sessionId, String endPointUrl,Report currentReport) throws ClientProtocolException, IOException, ParseException, InterruptedException, java.text.ParseException {
		String jobsUrl = endPointUrl+"data/v42.0/wave/dataflowjobs";
		String data =httpUitls.getData(jobsUrl,sessionId);
		Map<String,DataflowJob> flowMap = null;
		if (data != null) {
			flowMap =getReportDataflowJobDetails(data,currentReport);
			
		}
		return flowMap;
	}
	
	private Map<String,DataflowJob> getDataFlowJobs(String sessionId, String endPointUrl) throws ClientProtocolException, IOException, ParseException, InterruptedException, java.text.ParseException {
		String jobsUrl = endPointUrl+"data/v42.0/wave/dataflowjobs";
		String data =httpUitls.getData(jobsUrl,sessionId);
		Map<String,DataflowJob> flowMap = null;
		if (data != null) {
			flowMap =getDataflowJobDetails(data);
			
		}
		return flowMap;
	}
	
	private String getServiceEndPointUrl(String endPointUrl) {
		String transformedEdnPointUrl = endPointUrl;
		transformedEdnPointUrl =endPointUrl.substring(0, endPointUrl.indexOf("Soap/"));
		return transformedEdnPointUrl;
	}
	
	private Map<String,DataflowJob>  getReportDataflowJobDetails(String data,Report currentReport) throws ParseException, InterruptedException, java.text.ParseException, JsonParseException, JsonMappingException, IOException {
		 ObjectMapper mapper = new ObjectMapper();
		    Map<String,DataflowJob> flowMap = new HashMap<String,DataflowJob>();
		    DataflowContainer dataflow =mapper.readValue(data, DataflowContainer.class); 
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    if (dataflow != null) {
		    	Date jobDate = null;
		    	Date today = dateFormat.parse(dateFormat.format(Calendar.getInstance().getTime()));
		    	for (DataflowJob job : dataflow.getDataflowJobs()) {
		    		if (job.getDataflow() != null) {
		    			System.out.println(job.getDataflow().getName());
		    		}
		    		if (FILE_UPLOAD_JOBTYPE.equalsIgnoreCase(job.getJobType())) {
			    		jobDate = dateFormat.parse(job.getCreatedDate());
			    		if (job.getLabel().startsWith(currentReport.getName()) && jobDate.compareTo(today) == 0 && ("Queued".equalsIgnoreCase(job.getStatus()) || "Running".equalsIgnoreCase(job.getStatus()))) {
			    			flowMap.put(currentReport.getName(), job);
			    		} 
		    		} else if (USER_JOBTYPE.equalsIgnoreCase(job.getJobType())) {
			    		if (job.getDataflow() != null && job.getDataflow().getName().equals(currentReport.getFlowName())) {
			    			System.out.println(job.getDataflow().getId());
			    			flowMap.put(currentReport.getFlowName(), job);
			    		}
		    		}
		    		
		    	}
		    }
		    System.out.println(dataflow);
		    return flowMap;
	}
	
	private Map<String,DataflowJob>  getDataflowJobDetails(String data) throws ParseException, InterruptedException, java.text.ParseException, JsonParseException, JsonMappingException, IOException {
	    //System.out.println("data---->"+data);
	    ObjectMapper mapper = new ObjectMapper();
	    Map<String,DataflowJob> flowMap = new HashMap<String,DataflowJob>();
	    DataflowContainer dataflow =mapper.readValue(data, DataflowContainer.class); 
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    if (dataflow != null) {
	    	Date jobDate = null;
	    	Date today = dateFormat.parse(dateFormat.format(Calendar.getInstance().getTime()));
	    	for (DataflowJob job : dataflow.getDataflowJobs()) {
	    		if (job.getDataflow() != null) {
	    			System.out.println(job.getDataflow().getName());
	    		}
	    		jobDate = dateFormat.parse(job.getCreatedDate());
	    		if (job.getLabel().startsWith("Pace Upload flow") && jobDate.compareTo(today) == 0 && ("Queued".equalsIgnoreCase(job.getStatus()) || "Running".equalsIgnoreCase(job.getStatus()))) {
	    			flowMap.put("Pace", job);
	    		} 
	    		if (job.getDataflow() != null && job.getDataflow().getName().equals("Marketing_and_Sales_Performance")) {
	    			System.out.println(job.getDataflow().getId());
	    			flowMap.put("MarketingFlow", job);
	    		}
	    		if (flowMap.size() >= 2) {
	    			break;
	    		}
	    	}
	    }
	    System.out.println(dataflow);
	    return flowMap;
	}

}
