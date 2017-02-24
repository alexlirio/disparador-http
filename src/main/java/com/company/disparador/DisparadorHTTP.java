package com.company.disparador;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


public class DisparadorHTTP {

	private final static Logger log = Logger.getLogger(DisparadorHTTP.class);
	
	private static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_METHOD_POST = "POST";
	private static final String HTTP_METHOD_PUT = "PUT";
	private static final String HTTP_METHOD_DELETE = "DELETE";
	
	public final static String PREFIX_ARG = "-";
	public final static String HTTP_HOST_ARG = "http_host";
	public final static String HTTP_VERB_ARG = "http_verb";
	public final static String HTTP_HEADER_ARG = "http_header";
	public final static String HTTP_BODY_ARG = "http_body";
	
	private String httpHost;
	private String httpVerb;
	private List<BasicHeader> httpBasicHeaders;
	private String httpBody;
	
	
	public static void main(String[] args) {
		
		DisparadorHTTP disparadorHTTP = new DisparadorHTTP();
		Map<String, String> argumentos = new TreeMap<String, String>();
		
		for (int i = 0; i < args.length-1; i=i+2) {
			argumentos.put(args[i], args[i+1]);
		}
		
		disparadorHTTP.disparar(argumentos);
	}

	public String disparar() {
		carregaArgumentos(new TreeMap<String, String>());
		return send();
	}

	public String disparar(Map<String, String> argumentos) {
		carregaArgumentos(argumentos);
		return send();
	}
	
	private void carregaArgumentos(Map<String, String> argumentos) {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("cfg/config.properties");
			prop.load(input);
			httpHost = argumentos.get(PREFIX_ARG + HTTP_HOST_ARG) != null ? argumentos.get(PREFIX_ARG + HTTP_HOST_ARG) : prop.getProperty(HTTP_HOST_ARG);
			httpVerb = argumentos.get(PREFIX_ARG + HTTP_VERB_ARG) != null ? argumentos.get(PREFIX_ARG + HTTP_VERB_ARG) : prop.getProperty(HTTP_VERB_ARG);
			
			httpBasicHeaders = new ArrayList<BasicHeader>();
			for (Map.Entry<?, ?> entry : argumentos.entrySet()) {
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				if (key.startsWith(PREFIX_ARG + HTTP_HEADER_ARG)) {
					httpBasicHeaders.add(getBasicHeader(value));
				}
			}
			if (httpBasicHeaders.isEmpty()) {
				for (Map.Entry<?, ?> entry : prop.entrySet()) {
					String key = (String)entry.getKey();
					String value = (String)entry.getValue();
					if (key.startsWith(HTTP_HEADER_ARG)) {
						httpBasicHeaders.add(getBasicHeader(value));
					}
				}
			}
			
			httpBody = argumentos.get(PREFIX_ARG + HTTP_BODY_ARG) != null ? argumentos.get(PREFIX_ARG + HTTP_BODY_ARG) : prop.getProperty(HTTP_BODY_ARG);
			
		} catch (IOException e) {
			log.error(e.getClass().getSimpleName() + " in " + this.getClass().getSimpleName() , e);
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error(e.getClass().getSimpleName() + " in " + this.getClass().getSimpleName() , e);
					e.printStackTrace();
				}
			}
		}
		
		log.info("HTTP Request " + HTTP_HOST_ARG + " = " + httpHost);
		log.info("HTTP Request " + HTTP_VERB_ARG + " = " + httpVerb);
		for (BasicHeader httpBasicHeader : httpBasicHeaders) {
			log.info("HTTP Request " + HTTP_HEADER_ARG + " = " + httpBasicHeader);
		}
		log.info("HTTP Request " + HTTP_BODY_ARG + " = " + httpBody);
	}
	
	private BasicHeader getBasicHeader(String httpBasicHeader) {
		String key = httpBasicHeader.split(":")[0];
		String value = "";
		for (int i = 1; i < httpBasicHeader.split(":").length; i++) {
			value = value.concat(httpBasicHeader.split(":")[i]).concat(":");
		}
		if (value.endsWith(":")) {
			value = value.substring(0, value.length()-1);
		}
		return new BasicHeader(key, value);
	}

	private String send() {
		String ret = "";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;

		try {
			
			if (httpVerb.equalsIgnoreCase(HTTP_METHOD_GET)) {
				HttpGet httpGet = new HttpGet(httpHost);
				for (BasicHeader httpBasicHeader : httpBasicHeaders) {
					httpGet.setHeader(httpBasicHeader);
				}
				httpResponse = httpClient.execute(httpGet);

			} else if (httpVerb.equalsIgnoreCase(HTTP_METHOD_POST)) {
				HttpPost httpPost = new HttpPost(httpHost);
				for (BasicHeader httpBasicHeader : httpBasicHeaders) {
					httpPost.setHeader(httpBasicHeader);
				}
				httpPost.setEntity(new StringEntity(httpBody));
				httpResponse = httpClient.execute(httpPost);
				
			} else if (httpVerb.equalsIgnoreCase(HTTP_METHOD_PUT)) {
				HttpPut httpPut = new HttpPut(httpHost);
				for (BasicHeader httpBasicHeader : httpBasicHeaders) {
					httpPut.setHeader(httpBasicHeader);
				}
				httpPut.setEntity(new StringEntity(httpBody));
				httpResponse = httpClient.execute(httpPut);
				
			} else if (httpVerb.equalsIgnoreCase(HTTP_METHOD_DELETE)) {
				HttpDelete httpDelete = new HttpDelete(httpHost);
				for (BasicHeader httpBasicHeader : httpBasicHeaders) {
					httpDelete.setHeader(httpBasicHeader);
				}
				httpResponse = httpClient.execute(httpDelete);

			} else {
				ret = String.format("Parameter HTTP Verb incorrect or not implemented. HTTP Verb: %s", httpVerb);
				log.error(String.format("Parameter HTTP Verb incorrect or not implemented. HTTP Verb: %s", httpVerb));
			}
			
			if (httpResponse != null) {
				StatusLine statusLine = httpResponse.getStatusLine();
				if (statusLine != null) {
					int statusCode = statusLine.getStatusCode();
					HttpEntity entity = httpResponse.getEntity();
					log.info(String.format("HTTP Response status code = %s", statusCode));
					if ((statusCode >= 200) && (statusCode < 300)) {
						log.info("HTTP Response http_header Content-Type: " + entity.getContentType().getValue());
						ret = EntityUtils.toString(entity);
						log.info("HTTP Response " + HTTP_BODY_ARG + " = " + ret);
					} else {
						ret = "An error occurred on host: " + statusLine.toString();
						log.error("An error occurred on host: " + statusLine.toString());
					} 
				} else {
					ret = "An error occurred trying to connect on host. httpResponse.StatusLine is NULL.";
					log.error("An error occurred trying to connect on host. httpResponse.StatusLine is NULL.");
				}
			} else {
				ret = "An error occurred trying to connect on host. httpResponse is NULL.";
				log.error("An error occurred trying to connect on host. httpResponse is NULL.");
			}

		} catch (UnsupportedEncodingException e) {
			ret = String.format("An encoding error has occurred. %s", e);
			log.error(String.format("An encoding error has occurred. %s", e));
		} catch (ClientProtocolException e) {
			ret = String.format("A client protocol error has occurred. %s", e);
			log.error(String.format("A client protocol error has occurred. %s", e));
		} catch (IOException e) {
			ret = String.format("An IO error has occurred. %s", e);
			log.error(String.format("An IO error has occurred. %s", e));
		} finally {
			try {
				httpClient.close();
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (Exception e) {
				ret = String.format("An Exception error has occurred. %s", e);
				log.error(String.format("An Exception error has occurred. %s", e));
			}
		}

		return ret;
	}
	
}
