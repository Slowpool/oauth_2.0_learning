package com.swetlokognatsk.protected_resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.swetlokognatsk.protected_resource.adapters.DaoDatabase;
import com.swetlokognatsk.oauth_db.daos.AccessTokenDao;
import com.swetlokognatsk.protected_resource.adapters.jakarta.WordsDao;
import com.swetlokognatsk.protected_resource.ports.Database;
import com.swetlokognatsk.protected_resource.services.AccessTokenValidator;

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
        return new DaoDatabase(accessTokenDao, wordsDao);
    }

    @Bean
    AccessTokenValidator getAccessTokenValidator(final Database database) {
        return new AccessTokenValidator(database);
    }

}
