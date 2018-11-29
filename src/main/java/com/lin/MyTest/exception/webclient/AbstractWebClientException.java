package com.lin.MyTest.exception.webclient;

public abstract class AbstractWebClientException extends RuntimeException{

    private String responseCode;

    private String responseMessage;

    public AbstractWebClientException(String message, String responseCode, String responseMessage) {
        super(message, null, false, true);
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
