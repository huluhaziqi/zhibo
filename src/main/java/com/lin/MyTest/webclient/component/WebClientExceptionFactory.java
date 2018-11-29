package com.lin.MyTest.webclient.component;

import com.lin.MyTest.exception.webclient.AbstractWebClientException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WebClientExceptionFactory {

    public static AbstractWebClientException constructNetException(Class<? extends AbstractWebClientException> clazz,
                                                                   String responseCode, String responseMessage,
                                                                   String customMessage) {
        try {
            Constructor<? extends AbstractWebClientException> constructor = clazz.getConstructor(String.class, String.class,
                    String.class);
            AbstractWebClientException exception = constructor.newInstance(customMessage, responseCode, responseMessage);
            return exception;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
