package com.swetlokognatsk.protected_resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.swetlokognatsk.protected_resource.adapters.HibernatePostgresqlDatabase;
import com.swetlokognatsk.protected_resource.ports.Database;
import com.swetlokognatsk.protected_resource.services.AccessTokenVerifier;

@Configuration
public class AppConfig {

    @Bean
    Database getDatabase() {
        return new HibernatePostgresqlDatabase();
    }

    @Bean
    AccessTokenVerifier getAccessTokenVerifier(final Database database) {
        return new AccessTokenVerifier(database);
    }

}
