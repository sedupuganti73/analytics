package com.bnsf.analytics.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class HttpUtils {
	private static final String PROXY_URL="ftwrailproxy.bnsf.com";
	private static final int PROXY_PORT =8080;
	
	public  String getData(String url, String sessionId) throws ClientProtocolException, IOException  {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 
		HttpGet request = new HttpGet(url);
	    HttpHost proxy = new HttpHost(PROXY_URL,PROXY_PORT,"http"); 
	    List<Header> headerList = getHeaders(sessionId,true);
	    request.setHeaders(headerList.toArray(new Header[headerList.size()]));
	    request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
	    CloseableHttpResponse response= httpClient.execute(request);
	    HttpEntity entity =response.getEntity();
	    return EntityUtils.toString(entity, "UTF-8").trim();
	}
	
	public String patchData(String url, String sessionId,String jsonData) throws ParseException, IOException, InterruptedException, java.text.ParseException {
		return patchData(url,sessionId,jsonData,true);
	}
	public String patchData(String url, String sessionId,String jsonData, boolean addContentEncoding) throws ParseException, IOException, InterruptedException, java.text.ParseException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 
		StringEntity params = new StringEntity(jsonData,ContentType.APPLICATION_JSON); 
		HttpPost request = new HttpPost(url);
		HttpHost proxy = new HttpHost(PROXY_URL,PROXY_PORT,"http");
	    List<Header> headerList = getHeaders(sessionId,addContentEncoding);
	    request.setHeaders(headerList.toArray(new Header[headerList.size()]));
	    request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
	    request.setEntity(params);
	    CloseableHttpResponse response= httpClient.execute(request);
	    HttpEntity entity =response.getEntity();
	    return EntityUtils.toString(entity, "UTF-8").trim();
	}
	
	private List<Header> getHeaders(String sessionId, boolean addContentEncoding) {
		List<Header> headerList = new ArrayList<Header>();
	    headerList.add(getHeader("Authorization", "OAuth " + sessionId));
	    headerList.add(getHeader(HttpHeaders.CONTENT_TYPE,"application/json"));
	    if (addContentEncoding) {
	        headerList.add(getHeader(HttpHeaders.CONTENT_ENCODING,"gzip"));
	    }
	    headerList.add(getHeader(HttpHeaders.CACHE_CONTROL,"no-cache, must-revalidate, max-age=0, no-store, private"));
	    headerList.add(getHeader(HttpHeaders.CONNECTION,"Keep-Alive"));
	    return headerList;
	}
	
	private static Header getHeader(String name, String value) {
		return new BasicHeader(name, value);
	}

}
