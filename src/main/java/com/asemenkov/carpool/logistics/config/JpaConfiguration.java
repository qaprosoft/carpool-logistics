package com.asemenkov.carpool.logistics.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author asemenkov
 * @since Feb 16, 2018
 */
@Configuration
@PropertySource("classpath:datasource.properties")
@EnableJpaRepositories(basePackages = "com.asemenkov.carpool.logistics.repositories")
@EnableTransactionManagement
public class JpaConfiguration {

	@Autowired
	private Environment environment;

	@Bean
	public JpaTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}

	@Bean
	@Primary
	public DataSourceProperties dataSourceProperties() {
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.setDriverClassName(environment.getRequiredProperty("db.driverClassName"));
		dataSourceProperties.setUsername(environment.getRequiredProperty("db.username"));
		dataSourceProperties.setPassword(environment.getRequiredProperty("db.password"));
		dataSourceProperties.setUrl(environment.getRequiredProperty("db.url"));
		return dataSourceProperties;
	}

	@Bean
	public DataSource dataSource() {
		DataSourceProperties dataSourceProperties = dataSourceProperties();
		HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder //
				.create(dataSourceProperties.getClassLoader()) //
				.driverClassName(dataSourceProperties.getDriverClassName()) //
				.url(dataSourceProperties.getUrl()) //
				.username(dataSourceProperties.getUsername()) //
				.password(dataSourceProperties.getPassword()) //
				.type(HikariDataSource.class) //
				.build();
		dataSource.setMaximumPoolSize(environment.getRequiredProperty("db.maxPoolSize", Integer.class));
		return dataSource;
	}

	@Bean
	@Autowired
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setPackagesToScan("com.asemenkov.carpool.logistics.models.db");
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
		factoryBean.setJpaProperties(jpaProperties());
		return factoryBean;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	private Properties jpaProperties() {
		Properties prop = new Properties();
		prop.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		prop.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.method"));
		prop.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
		prop.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
		prop.put("hibernate.default_schema", environment.getRequiredProperty("hibernate.default_schema"));
		return prop;
	}

}
