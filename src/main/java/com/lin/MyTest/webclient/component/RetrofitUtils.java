package com.lin.MyTest.webclient.component;

import com.lin.MyTest.webclient.annotation.RetrofitEnqueue;

import java.lang.annotation.Annotation;

public class RetrofitUtils {

    public static boolean isEnqueue(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == RetrofitEnqueue.class) {
                return true;
            }
        }
        return false;
    }
}
