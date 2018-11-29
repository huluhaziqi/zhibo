package com.lin.MyTest.config;

import com.lin.MyTest.exception.webclient.WebClientException;
import com.lin.MyTest.webclient.WebClient;
import com.lin.MyTest.webclient.component.RetrofitCallAdapterFactory;
import com.lin.MyTest.webclient.component.RetrofitInterceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {


	@Value("${baseUrl}")
	private String baseUrl;

	@Bean
	public WebClient webClient() {
		OkHttpClient httpClient = new OkHttpClient().newBuilder()
				.addInterceptor(new RetrofitInterceptor(WebClientException.class)).build();
		Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(httpClient)
				.addConverterFactory(JacksonConverterFactory.create()).addCallAdapterFactory(
						new RetrofitCallAdapterFactory(WebClientException.class, "code", "200", "msg", "data"))
				.build();
		return retrofit.create(WebClient.class);


	}
}
