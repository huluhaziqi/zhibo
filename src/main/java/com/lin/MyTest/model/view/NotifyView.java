package com.lin.MyTest.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotifyView {

	@JsonProperty("code")
	private int code = 0;//腾讯云要求必须返回0.

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
