package com.lin.MyTest.exception;

public abstract class AbstractClientException extends RuntimeException {

	private static final long serialVersionUID = 8951443249285282830L;

	private int code;

	protected AbstractClientException(int code, String message) {
		super(message, null, false, false);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
