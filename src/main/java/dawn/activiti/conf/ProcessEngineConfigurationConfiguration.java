package dawn.activiti.conf;

import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.DataSourceProcessEngineAutoConfiguration;
import org.activiti.spring.boot.JpaProcessEngineAutoConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;


@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(value = { DataSourceProcessEngineAutoConfiguration.class})
public class ProcessEngineConfigurationConfiguration{
	
	
	private IdGenerator idGenerator;


	@Bean
	public IdGenerator idGenerator() {
		return new StrongUuidGenerator();
	}
	

	public ProcessEngineConfigurationConfiguration(IdGenerator idGenerator) {
		super();
		this.idGenerator = idGenerator;
	}

	public ProcessEngineConfigurationConfiguration() {
	}

	@Bean
	public ProcessEngineConfigurationConfigurer configure() {
		return config -> {
			config.setIdGenerator(idGenerator);
		};
	}

}
