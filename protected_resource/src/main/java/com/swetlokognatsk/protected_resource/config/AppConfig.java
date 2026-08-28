package com.swetlokognatsk.protected_resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.swetlokognatsk.protected_resource.adapters.HibernatePostgresqlDatabase;
import com.swetlokognatsk.protected_resource.adapters.hibernate.AccessTokenDao;
import com.swetlokognatsk.protected_resource.ports.Database;
import com.swetlokognatsk.protected_resource.services.AccessTokenVerifier;

@Configuration
public class AppConfig {

    @Bean
    // TODO it's package-private because it's accessed via reflection?
    AccessTokenDao getAccessTokenDao() {
        return new AccessTokenDao();
    }

    @Bean
    Database getDatabase(final AccessTokenDao accessTokenDao) {
        return new HibernatePostgresqlDatabase(accessTokenDao);
    }

    @Bean
    AccessTokenVerifier getAccessTokenVerifier(final Database database) {
        return new AccessTokenVerifier(database);
    }

}
