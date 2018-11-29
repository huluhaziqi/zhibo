package com.lin.MyTest.model.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IMNotifyView {

    @JsonProperty("ActionStatus")
	private String ActionStatus;
    @JsonProperty("ErrorCode")
	private Integer ErrorCode;
    @JsonProperty("ErrorInfo")
	private String ErrorInfo;
	
	public IMNotifyView(){
		
	}
	
	public IMNotifyView(String actionStatus, Integer errorCode, String errorInfo) {
		super();
		ActionStatus = actionStatus;
		ErrorCode = errorCode;
		ErrorInfo = errorInfo;
	}

	public String getActionStatus() {
		return ActionStatus;
	}
	public void setActionStatus(String actionStatus) {
		ActionStatus = actionStatus;
	}
	public Integer getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(Integer errorCode) {
		ErrorCode = errorCode;
	}
	public String getErrorInfo() {
		return ErrorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		ErrorInfo = errorInfo;
	}
}
