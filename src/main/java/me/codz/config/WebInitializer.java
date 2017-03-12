package me.codz.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.IntrospectorCleanupListener;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/5/30
 * <p>Time: 22:55
 * <p>Version: 1.0
 */
public class WebInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(ApplicationConfig.class);
		servletContext.addListener(IntrospectorCleanupListener.class);
		servletContext.addListener(new ContextLoaderListener(rootContext));

		//CharacterEncodingFilter
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		FilterRegistration filterRegistration =
				servletContext.addFilter("characterEncodingFilter", characterEncodingFilter);
		filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

		////OpenEntityManagerInViewFilter
		//OpenEntityManagerInViewFilter openEntityManagerInViewFilter = new OpenEntityManagerInViewFilter();
		//FilterRegistration filterRegistrationOpenEntityManagerInViewFilter =
		//        servletContext.addFilter("openEntityManagerInViewFilter", openEntityManagerInViewFilter);
		//
		////filterRegistrationOpenEntityManagerInViewFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
		//filterRegistrationOpenEntityManagerInViewFilter.setInitParameter("entityManagerFactoryBeanName", "entityManagerFactory");
		//filterRegistrationOpenEntityManagerInViewFilter.addMappingForUrlPatterns(null, false, "/*");


		//Spring MVC
		AnnotationConfigWebApplicationContext springMvcContext = new AnnotationConfigWebApplicationContext();
		springMvcContext.register(MVCConfig.class);

		//DispatcherServlet
		DispatcherServlet dispatcherServlet = new DispatcherServlet(springMvcContext);
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

		ServletRegistration.Dynamic dynamic = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
		dynamic.setLoadOnStartup(1);
		dynamic.addMapping("/");
	}

}
