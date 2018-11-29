package com.lin.MyTest.interceptor;

import com.lin.MyTest.interceptor.annotation.AdminRequired;
import com.lin.MyTest.interceptor.annotation.HostRequired;
import com.lin.MyTest.interceptor.annotation.LoginRequired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);
        HostRequired hostRequired = handlerMethod.getMethodAnnotation(HostRequired.class);
        AdminRequired adminRequired = handlerMethod.getMethodAnnotation(AdminRequired.class);


        if (loginRequired != null) {
        }

        if (adminRequired != null) {
        }

        if (hostRequired != null) {
        }
        return true;
    }

}
