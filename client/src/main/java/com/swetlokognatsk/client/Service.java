package com.swetlokognatsk.client;

import java.util.UUID;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public final class Service implements ApplicationContextAware {

    private ApplicationContext ctx;
    
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    
    @Bean
    Service getService() {
        return new Service();
    }

    Service() {    
    }

    public String generateState() {
		return UUID.randomUUID().toString();
	}

}
