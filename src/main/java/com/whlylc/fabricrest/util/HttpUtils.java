/*
 * Copyright (c) 2013,  All rights reserved.
 */
package com.whlylc.fabricrest.util;

import com.whlylc.fabricrest.vo.Result;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author zeal
 */
public class HttpUtils {
	
	public static final String METHOD_POST = "POST";
	
	public static final String METHOD_GET = "GET";
	
	public static boolean isPostRequest(String httpRequest) {
		return httpRequest.startsWith(METHOD_POST);
	}
	
	public static String getPostBody(String httpRequest) {
		String token = "\r\n\r\n";
		int index = httpRequest.indexOf(token);
		if (index < 0) {
			return "";
		}
		String body = httpRequest.substring(index + token.length());
		return body;
	}
	
	public static String getContentTypeCharset(String contentType, String defaultCharset) {
		//String contentType = conn.getContentType();
		if (contentType == null || contentType.length() <= 0) {
			return defaultCharset;
		}
		String[] elems = contentType.split(";");
		for (String elem  : elems) {
			int index = elem.indexOf('=');
			if (index < 0) {
				continue;
			}
			String field = elem.substring(0, index).trim();
			if ("charset".equalsIgnoreCase(field)) {
				String value = elem.substring(index + 1);
				if (value != null && value.length() > 0) {
					return value;
				}
			}
		}
		return defaultCharset;
	}
	

	
	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	    }

	    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	    }

	    public X509Certificate[] getAcceptedIssuers() {
	        return new X509Certificate[]{};
	    }

	}
	
	private static class TrustAnyHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {
	        return true;
	    }
	}	
	
	
	public static HttpURLConnection getHttpURLConnection(String urlstr) throws Exception {
		URL url = new URL(urlstr);
		return getHttpURLConnection(url);
	}
	
	public static HttpURLConnection getHttpURLConnection(URL url) throws Exception {
		//URL url = new URL(urlstr);
		if ("https".equalsIgnoreCase(url.getProtocol())) {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
			    new java.security.SecureRandom());

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(); 
		    conn.setSSLSocketFactory(sc.getSocketFactory());
		    conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
		    return conn;
		}
		else {
		    return (HttpURLConnection) url.openConnection(); 
		}
	}
	
	/**
	 * 
	 * @param urlstr
	 * @param requestMethod
	 * @param params
	 * @param contentType
	 * @param requestCharset
	 * @param responseCharset 如果返回的content
	 * @return
	 * @throws IOException
	 */
	public static Result<String> request(String urlstr, String requestMethod, 
		Map<String, String> params, String contentType,
		String requestCharset, String responseCharset) throws IOException {
		
		String paramString = getUrlParameterString(params, requestCharset);
		return request(urlstr, requestMethod, paramString, contentType, requestCharset, responseCharset);
	}
	
	public static Result<String> request(String urlstr,
										 String requestMethod, Map<String, String> requestHeaders,
										 Map<String, String> params, String contentType,
										 String requestCharset, String responseCharset) throws IOException {
		
		String paramString = getUrlParameterString(params, requestCharset);
		return request(urlstr, requestMethod, requestHeaders, paramString, contentType, requestCharset, responseCharset);
	}
	
	
	/**
	 * 
	 * @param urlstr
	 * @param requestMethod
	 * @param contentType
	 * @param requestCharset
	 * @param responseCharset 如果返回的content
	 * @return
	 * @throws IOException
	 */
	public static Result<String> request(String urlstr, String requestMethod, 
		String requestParamStr, String contentType,
		String requestCharset, String responseCharset) throws IOException {
		
		return request(urlstr, requestMethod, null, requestParamStr, contentType, requestCharset, responseCharset);
	}
	
	/**
	 * 
	 * @param urlstr
	 * @param requestMethod
	 * @param contentType
	 * @param requestCharset
	 * @param responseCharset 如果返回的content
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static Result<String> request(String urlstr, 
		String requestMethod, Map<String, String> requestHeaders, 
		String requestParamStr, String contentType,
		String requestCharset, String responseCharset) throws IOException {
		
		//获取链接.
		HttpURLConnection conn = null;
		try {
			conn = getHttpURLConnection(urlstr);
		} 
		catch (Exception e) {
			throw new IOException("Failed to get url connection, url=" + urlstr);
		}
		if (contentType != null && contentType.length() > 0) {
			conn.setRequestProperty("Content-Type", contentType);
		}
		
		if (requestHeaders != null && requestHeaders.size() > 0) {
			for (Map.Entry<String, String> headerEntry : requestHeaders.entrySet()) {
				conn.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
			}
		}
		
		conn.setDoOutput(true);// 打开写入属性 
		conn.setDoInput(true);// 打开读取属性 
		conn.setRequestMethod(requestMethod);// 设置提交方法 
		conn.setConnectTimeout(100000);// 连接超时时间 
		conn.setReadTimeout(100000); 
		conn.connect(); 
		
	
		//String paramString = getUrlParameterString(params);
		BufferedWriter out = null;
		InputStream inputStream = null;
		//Reader reader = null;
		try {
			if (METHOD_POST.equalsIgnoreCase(requestMethod)) {
			    out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), requestCharset)); 
			    out.write(requestParamStr);//要post的数据，多个以&符号分割 
			    out.flush(); 
			    out.close(); 	
			    out = null;
			}
		    String charset = getContentTypeCharset(conn.getContentType(), responseCharset);
            int contentLength = conn.getContentLength();
            String response = "";
            
            String contentEncoding = conn.getContentEncoding();
            
            inputStream = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
            boolean zip = false;
            if (StringUtils.isNotEmpty(contentEncoding)) {
            	zip = true;
	            if ("gzip".equalsIgnoreCase(contentEncoding)) {
	            	inputStream = new GZIPInputStream(inputStream);
	            }
	            else if ("deflate".equalsIgnoreCase(contentEncoding) || "zlib".equalsIgnoreCase(contentEncoding)) {
	            	inputStream = new InflaterInputStream(inputStream, new Inflater(true));
	            }
            }
            
            if (contentLength == -1 || zip) {
            	response = IOUtils.toString(inputStream, charset);
            }
            else if (contentLength > 0) {
            	byte[] bytes = new byte[contentLength];
            	IOUtils.readFully(inputStream, bytes);
            	response = new String(bytes, charset);
            }
			int respCode = conn.getResponseCode();
		    Result<String> result = new Result<String>(respCode);
		    result.setResultEntity(response);
		    return result;
		}
		finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(inputStream);
			IOUtils.close(conn);
		}
	}
	
	/**
	 * Download file
	 * @param urlstr
	 * @param targetDir
	 * @param strictContentDisposition
	 * @return
	 * @throws IOException
	 */
	public static Result<File> downloadFile(String urlstr, File targetDir, 
		boolean strictContentDisposition) throws IOException {
		
		//获取链接.
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(urlstr);
			conn = getHttpURLConnection(url);
		} 
		catch (Exception e) {
			throw new IOException("Failed to get url connection, url=" + urlstr);
		}
		conn.setDoOutput(true);// 打开写入属性 
		conn.setDoInput(true);// 打开读取属性 
		conn.setRequestMethod(METHOD_GET);// 设置提交方法 
		conn.setConnectTimeout(100000);// 连接超时时间 
		conn.setReadTimeout(100000); 
		conn.connect();
		int respCode = conn.getResponseCode();
		Result<File> result = new Result<>();
		result.setResultCode(respCode);
		if (respCode != 200) {
			return result;
		}
		InputStream in = conn.getInputStream();
		int contentLength = conn.getContentLength();
		String contentDisposition = conn.getHeaderField("Content-disposition");
		File targetFile = null;
		
		if (StringUtils.isBlank(contentDisposition)) {
			if (strictContentDisposition) {
			    throw new IOException("Content-disposition is missing in response");
			}
			else {
				
				//URL能提取到文件名就用这个名字,为空就生成一个UUID名字
				String fileName = URLUtils.getURLFileName(url);
				if (StringUtils.isEmpty(fileName)) {
					fileName = StringUtils.generateUUID();
				}
				targetFile = new File(targetDir, fileName);
			}
		}
		else {
			String[] array = StringUtils.split(contentDisposition, ';');
			for (String item : array) {
				item = item.trim();
				String[] values = StringUtils.split(item, '=');
				if (values.length != 2) {
					continue;
				}
				String key = values[0].trim();
				if ("filename".equals(key)) {
					String fileName = StringUtils.strip(values[1], "\" ");
					targetFile = new File(targetDir, fileName);
					break;
				}
			}
			if (targetFile == null) {
				if (strictContentDisposition) {
				    throw new IOException("Content-disposition " + contentDisposition  + " does not contain filename");
				}
				else {
					targetFile = new File(targetDir, StringUtils.generateUUID());
				}
			}
		}
		result.setResultEntity(targetFile);
		if (contentLength > 0) {
			FileUtils.writeFully(targetFile, in, contentLength);
		}
		else {
			FileUtils.copyInputStreamToFile(in, targetFile);
		}
		return result;
	}
	


	
	public static String getUrlParameterString(Map<String, String> params) {
		return getUrlParameterString(params, "UTF-8");
	}
	
	public static String getUrlParameterString(Map<String, String> params, String charset) {
		return URLUtils.getURLQueryString(params, false, true, charset);
	}
	
	/**
	 * HttpUrlConnection use content type=application/x-www-form-urlencoded to submit the form by default
	 * @param url
	 * @param paramMap
	 * @return
	 * @throws IOException
	 */
	public static Result<String> post(String url, Map<String,String> paramMap) throws IOException {
		return request(url, METHOD_POST, paramMap, "application/x-www-form-urlencoded;charset=UTF-8", "UTF-8", "UTF-8");
	}
	
	/**
	 * HttpUrlConnection use content type=application/x-www-form-urlencoded to submit the form by default
	 * @param url
	 * @param paramMap
	 * @return
	 * @throws IOException
	 */
	public static Result<String> post(String url, Map<String,String> headerMap, Map<String,String> paramMap) throws IOException {
		return request(url, METHOD_POST, headerMap, paramMap, "application/x-www-form-urlencoded;charset=UTF-8", "UTF-8", "UTF-8");
	}
	
	/**
	 * Post and content type is text/plain and charset is UTF-8
	 * @param url
	 * @param requestBody
	 * @return
	 * @throws IOException
	 */
	public static Result<String> post(String url, String requestBody) throws IOException {
		return request(url, METHOD_POST, requestBody, "text/plain", "UTF-8", "UTF-8");
	}
	
	/**
	 * Post request body and charset is UTF-8
	 * @param url
	 * @param requestBody
	 * @return
	 * @throws IOException
	 */
	public static Result<String> post(String url, String requestBody, String contentType) throws IOException {
		return request(url, METHOD_POST, requestBody, contentType, "UTF-8", "UTF-8");
	}
	
	public static Result<String> postForm(String url, String requestBody) throws IOException {
		return request(url, METHOD_POST, requestBody, "application/x-www-form-urlencoded;charset=UTF-8", "UTF-8", "UTF-8");
	}
	
	public static Result<String> get(String url) throws IOException {
		return request(url, METHOD_GET, "", null, "UTF-8", "UTF-8");
	}	
	
	public static void main(String[] args) throws Exception {
		
	}

}
