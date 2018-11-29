package com.lin.MyTest.exception.webclient;

public class WebClientException extends AbstractWebClientException {

    public WebClientException(String message, String responseCode, String responseMessage) {
        super(message, responseCode, responseMessage);
    }
}
