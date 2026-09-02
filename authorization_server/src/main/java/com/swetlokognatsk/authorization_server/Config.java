package com.swetlokognatsk.authorization_server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.swetlokognatsk.authorization_server.daos.ClientsDao;

// TODO difference between @SpringBootConfiguration and @Configuration?
@Configuration
public class Config {

    @Bean
    public ClientsDao getClientsDao() {
        return new ClientsDao();
    }

}
