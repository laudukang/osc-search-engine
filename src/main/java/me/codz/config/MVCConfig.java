package me.codz.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.List;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/5/30
 * <p>Time: 22:55
 * <p>Version: 1.0
 */
@Configuration
@ComponentScan(basePackages = "me.codz.controller", useDefaultFilters = false, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class, ControllerAdvice.class})
})
public class MVCConfig extends WebMvcConfigurationSupport {

	@Bean
	public ViewResolver viewResolver() {
		final InternalResourceViewResolver bean = new InternalResourceViewResolver();
		bean.setViewClass(JstlView.class);
		bean.setPrefix("/WEB-INF/view/");
		bean.setSuffix(".jsp");
		bean.setOrder(1);
		return bean;
	}

	/**
	 * How to Pretty Print Your JSON With Spring and Jackson
	 * http://springinpractice.com/2013/11/01/how-to-pretty-print-your-json-with-spring-and-jackson
	 */
	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(converter());
		addDefaultHttpMessageConverters(converters);
	}

	@Bean
	public MappingJackson2HttpMessageConverter converter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		/**
		 * see http://websystique.com/springmvc/spring-mvc-requestbody-responsebody-example/
		 * 2016年1月22日15:52:08
		 * laudukang
		 */
		converter.setObjectMapper(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
		return converter;
	}

	//@Bean
	//public StringHttpMessageConverter stringHttpMessageConverter() {
	//    StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
	//    return stringHttpMessageConverter;
	//}
}

