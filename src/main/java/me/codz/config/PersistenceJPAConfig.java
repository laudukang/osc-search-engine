package me.codz.config;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/1/22
 * <p>Time: 16:25
 * <p>Version: 1.0
 */

@Configuration
@PropertySource({"classpath:persistence-mysql.properties"})
@EnableJpaRepositories(basePackages = {"me.codz.domain", "me.codz.repository"})
@ComponentScan({"me.codz.domain", "me.codz.repository"})
@EnableTransactionManagement
@EnableJpaAuditing
public class PersistenceJPAConfig {

	@Autowired
	private Environment environment;

	public PersistenceJPAConfig() {
		super();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("me.codz.domain");
		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(jpaProperties());
		return em;
	}

	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(Preconditions.checkNotNull(environment.getProperty("jdbc.driverClassName")));
		dataSource.setUrl(Preconditions.checkNotNull(environment.getProperty("jdbc.url")));
		dataSource.setUsername(Preconditions.checkNotNull(environment.getProperty("jdbc.user")));
		dataSource.setPassword(Preconditions.checkNotNull(environment.getProperty("jdbc.pass")));
		return dataSource;
	}

	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		//transactionManager.setRollbackOnCommitFailure(false);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}


	final Properties jpaProperties() {
		final Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		hibernateProperties.setProperty("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
		hibernateProperties.setProperty("hibernate.format_sql", environment.getProperty("hibernate.format_sql"));
		//hibernateProperties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
		hibernateProperties.setProperty("hibernate.globally_quoted_identifiers", environment.getProperty("hibernate.globally_quoted_identifiers"));
		return hibernateProperties;
	}

	//@Bean
	//public HandlerExceptionResolver MyExceptionHandler(){
	//
	//}
}