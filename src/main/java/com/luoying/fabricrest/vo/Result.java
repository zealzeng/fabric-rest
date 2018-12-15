/*
 * Copyright (c) 2012,  All rights reserved.
 */
package com.luoying.fabricrest.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Common result object, we can use it as function returned value,json output and etc
 * @author Zed
 * @since 2012-4-13
 */
public class Result<T> implements Serializable {
	
	public static final String RESULT_KEY = "result";
	
	/** SUID */
	private static final long serialVersionUID = 7797630558230719229L;
	
	/** Result code, we can define zero as success and others are error codes */
	private int resultCode = 0;
	
	/** Result message or decription */
	private String resultMessage = null;
	
	/** Result entity */
	private T resultEntity = null;
	
	public Result() {
	}
	
	public Result(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public Result(int resultCode, String resultMessage) {
		this(resultCode, resultMessage, null);
	}
	
	public Result(int resultCode, T resultEntity) {
		this(resultCode, null, resultEntity);
	}
	
	public Result(int resultCode, String resultMessage, T resultEntity) {
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.resultEntity = resultEntity;
	}
	
	/**
	 * @return the resultCode
	 */
	public int getResultCode() {
		return resultCode;
	}
	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	
	/**
	 * @return the resultMessage
	 */
	public String getResultMessage() {
		return resultMessage;
	}
	/**
	 * @param resultMessage the resultMessage to set
	 */
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	/**
	 * @return the resultEntity
	 */
	public T getResultEntity() {
		return resultEntity;
	}
	/**
	 * @param resultEntity the resultEntity to set
	 * 夸张,return this; 就不是JavaBean规范了
	 */
	public void setResultEntity(T resultEntity) {
		this.resultEntity = resultEntity;
	}	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Result:[resultCode=").append(this.resultCode);
		sb.append(", resultMessage=").append(this.resultMessage);
		sb.append(",resultEntity=").append(this.resultEntity);
		sb.append(']');
		return sb.toString();
	}
	
	public Map<String,Object> toMap() {
		Map<String, Object> map = new HashMap<>(3);
		map.put("resultCode", this.resultCode);
		map.put("resultMessage", this.resultMessage);
		map.put("resultEntity", this.resultEntity);
		return map;
	}
	
	
}
