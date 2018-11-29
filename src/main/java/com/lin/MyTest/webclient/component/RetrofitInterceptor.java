package com.lin.MyTest.webclient.component;

import com.lin.MyTest.exception.webclient.AbstractWebClientException;
import okhttp3.Interceptor;
import okhttp3.Request;

import java.io.IOException;

public class RetrofitInterceptor implements Interceptor{
    private Class<? extends AbstractWebClientException> exceptionClass;

    public RetrofitInterceptor(Class<? extends AbstractWebClientException> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request().newBuilder().header("Accept", "application/json").build();
        okhttp3.Response response = chain.proceed(request);

        if (!response.isSuccessful()) {
            String code = String.valueOf(response.code());
            String msg = response.message();
            String url = request.url().toString();
            AbstractWebClientException exception = WebClientExceptionFactory.constructNetException(exceptionClass, code, msg,
                    "third party request fail: " + url);
            response.close();
            throw exception;
        }
        return response;
    }
}
