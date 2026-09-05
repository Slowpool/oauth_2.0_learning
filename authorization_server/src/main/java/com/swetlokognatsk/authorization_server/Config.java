package com.swetlokognatsk.authorization_server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.swetlokognatsk.authorization_server.daos.ClientsDao;
import com.swetlokognatsk.oauth_db.daos.AccessTokensDao;
import com.swetlokognatsk.oauth_db.daos.RefreshTokensDao;

// TODO difference between @SpringBootConfiguration and @Configuration?
@Configuration
public class Config {

    @Bean
    ClientsDao getClientsDao() {
        return new ClientsDao();
    }

    @Bean
    AccessTokensDao getAccessTokensDao() {
        return new AccessTokensDao();
    }

    @Bean
    RefreshTokensDao getRefreshTokensDao() {
        return new RefreshTokensDao();
    }

}
