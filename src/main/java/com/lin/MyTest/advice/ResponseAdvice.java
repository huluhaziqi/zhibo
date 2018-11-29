package com.lin.MyTest.advice;

import com.lin.MyTest.model.view.ApiWrapView;
import com.lin.MyTest.model.view.ErrorWrapView;
import com.lin.MyTest.model.view.IMNotifyView;
import com.lin.MyTest.model.view.NotifyView;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
        if (body instanceof ErrorWrapView || body instanceof NotifyView || body instanceof IMNotifyView
                || !selectedContentType.includes(MediaType.APPLICATION_JSON)) {
            return body;
        }
        return new ApiWrapView(body);
	}
}