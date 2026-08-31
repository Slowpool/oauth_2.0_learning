package com.swetlokognatsk.protected_resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.swetlokognatsk.protected_resource.adapters.HibernateDatabase;
import com.swetlokognatsk.protected_resource.adapters.hibernate.AccessTokenDao;
import com.swetlokognatsk.protected_resource.adapters.hibernate.WordsDao;
import com.swetlokognatsk.protected_resource.ports.Database;
import com.swetlokognatsk.protected_resource.services.AccessTokenVerifier;

@Configuration
class AppConfig {

    @Bean
    AccessTokenDao getAccessTokenDao() {
        return new AccessTokenDao();
    }

    @Bean
    WordsDao getWordsDao() {
        return new WordsDao();
    }

    @Bean
    Database getDatabase(final AccessTokenDao accessTokenDao, final WordsDao wordsDao) {
        return new HibernateDatabase(accessTokenDao, wordsDao);
    }

    @Bean
    AccessTokenVerifier getAccessTokenVerifier(final Database database) {
        return new AccessTokenVerifier(database);
    }

}
