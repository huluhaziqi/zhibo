package com.lin.MyTest.webclient.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.MyTest.exception.webclient.AbstractWebClientException;
import retrofit2.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public class RetrofitCallAdapterFactory extends CallAdapter.Factory{


    private Class<? extends AbstractWebClientException> exceptionClass;

    private String statusKey;
    private String statusSuccessValue;
    private String resultKey;
    private String errorMessageKey;

    /**
     * @param exceptionClass     请求失败时抛出的异常
     * @param statusKey          标志请求是否成功的jsonKey
     * @param statusSuccessValue 请求成功时的jsonValue
     * @param errorMessageKey    请求失败详细信息的jsonKey
     * @param resultKey          请求内容的jsonKey,可以为空,为空时则解析整个response
     */
    public RetrofitCallAdapterFactory(Class<? extends AbstractWebClientException> exceptionClass, String statusKey,
                                      String statusSuccessValue, String errorMessageKey, String resultKey) {
        this.exceptionClass = exceptionClass;
        this.statusKey = statusKey;
        this.statusSuccessValue = statusSuccessValue;
        this.errorMessageKey = errorMessageKey;
        this.resultKey = resultKey;
    }

    @Override
    public CallAdapter<?> get(final Type returnType, final Annotation[] annotations, Retrofit retrofit) {

        return new CallAdapter<Object>() {

            @Override
            public Type responseType() {
                return JsonNode.class;
            }

            @Override
            public <R> Object adapt(Call<R> call) {
                if (RetrofitUtils.isEnqueue(annotations)) {
                    // async
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(Call<R> call, Response<R> response) {
                        }

                        @Override
                        public void onFailure(Call<R> call, Throwable t) {
                        }
                    });
                    return null;
                }

                JsonNode responseBody;
                try {
                    // sync
                    @SuppressWarnings("unchecked")
                    Response<JsonNode> response = (Response<JsonNode>) call.execute();
                    responseBody = response.body();
                } catch (IOException e) {
                    String code = null;
                    String msg = e.getMessage();
                    String url = call.request().url().toString();
                    throw WebClientExceptionFactory.constructNetException(exceptionClass, code, msg, url);
                }
                // 判断status
                if (statusKey != null) {
                    String status = responseBody.get(statusKey).asText("");
                    if (!status.equals(statusSuccessValue)) {
                        // 构造exception
                        String code = status;
                        String msg = errorMessageKey == null ? null : responseBody.get(errorMessageKey).asText();
                        String url = call.request().url().toString();
                        throw WebClientExceptionFactory.constructNetException(exceptionClass, code, msg, url);
                    }
                }

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseData;
                // 获取data
                if (resultKey == null) {
                    // 获取全部responseBody
                    responseData = responseBody;
                } else {
                    // 只获取data部分
                    responseData = responseBody.get(resultKey);
                }

                // 处理空返回
                if (returnType.equals(Void.class)) {
                    return null;
                }

                if (returnType instanceof ParameterizedType) {
                    // 处理Map
                    if (((ParameterizedType) returnType).getRawType().equals(Map.class)) {
                        TypeReference<Map<Long, Boolean>> typeReference = new TypeReference<Map<Long, Boolean>>() {
                            @Override
                            public Type getType() {
                                return returnType;
                            }
                        };
                        return objectMapper.convertValue(responseData, typeReference);
                    }

                    //todo 改成map一样的处理方式
                    // 处理list
                    Class<?> realType = (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0];
                    try {
                        // 转为数组
                        Object object = objectMapper.convertValue(responseData,
                                Class.forName("[L" + realType.getName() + ";"));
                        Object[] objects = (Object[]) object;
                        return Arrays.asList(objects);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return objectMapper.convertValue(responseData, (Class<?>) returnType);
                }
            }
        };
    }
}
