package com.lin.MyTest.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorWrapView {

	@JsonProperty("code")
	private int code;

	@JsonProperty("msg")
	private String msg;

	public ErrorWrapView() {
	}

	public ErrorWrapView(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
