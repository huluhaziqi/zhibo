package com.lin.MyTest.exception;

public abstract class AbstractBaseException extends RuntimeException {

    private static final long serialVersionUID = 8951443249285282830L;

    private int code;
    private String msg;

    protected AbstractBaseException(int code, String message) {
        super(message, null, false, false);
        this.code = code;
        this.msg = message;
    }

    protected AbstractBaseException(int code, String message, String logMessage) {
        super(logMessage, null, false, false);
        this.code = code;
        this.msg = message;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
