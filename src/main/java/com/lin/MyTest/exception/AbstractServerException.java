package com.lin.MyTest.exception;

public abstract class AbstractServerException extends RuntimeException {

	private static final long serialVersionUID = -552430881967935058L;

	private int code;

	protected AbstractServerException(int code, String message) {
		super(message, null, false, true);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
