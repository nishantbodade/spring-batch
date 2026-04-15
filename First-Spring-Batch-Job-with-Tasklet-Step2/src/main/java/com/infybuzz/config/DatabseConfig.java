package com.infybuzz.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class DatabseConfig {

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource datasource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.eazyschooldatasource")
	public DataSource eazyschooldatasource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.postgresdatasource")
	public DataSource postgresdatasource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	public EntityManagerFactory postgresqlEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean lem = 
				new LocalContainerEntityManagerFactoryBean();
		
		lem.setDataSource(postgresdatasource());
		lem.setPackagesToScan("com.infybuzz.postgresql");
		lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		lem.afterPropertiesSet();
		
		return lem.getObject();
	}
	
	@Bean
	public EntityManagerFactory mysqlEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean lem = 
				new LocalContainerEntityManagerFactoryBean();
		
		lem.setDataSource(eazyschooldatasource());
		lem.setPackagesToScan("com.infybuzz.mysql");
		lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		lem.afterPropertiesSet();
		
		return lem.getObject();
	}
	
	@Bean
	@Primary
	public JpaTransactionManager jpaTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new 
				JpaTransactionManager();
		
		jpaTransactionManager.setDataSource(eazyschooldatasource());
		jpaTransactionManager.setEntityManagerFactory(mysqlEntityManagerFactory());
		
		return jpaTransactionManager;
	}

}
