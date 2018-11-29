package com.lin.MyTest.config;

import com.lin.MyTest.interceptor.LoginInterceptor;
import com.lin.MyTest.resolver.UserResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private BeanNameViewResolver beanNameViewResolver;

	@Autowired
	private LoginInterceptor loginInterceptor;

	@Autowired
	private UserResolver userResolver;

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.viewResolver(beanNameViewResolver);
		registry.enableContentNegotiation(new MappingJackson2JsonView());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(userResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
	}
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatterForFieldType(Date.class, new Formatter<Date>() {
			@Override
			public String print(Date object, Locale locale) {
				return String.valueOf(object.getTime());
			}

			@Override
			public Date parse(String text, Locale locale) throws ParseException {
				return new Date(Long.parseLong(text));
			}
		});
	}
}
