package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.stereotype.Repository;
import com.swetlokognatsk.authorization_server.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.daos.ClientsDao;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.authorization_server.ports.Database;

@Repository
public class JpaDatabase implements Database {

    private final ClientsDao clientsDao;

    public JpaDatabase(final ClientsDao clientsDao) {
        this.clientsDao = clientsDao;
    }

    public Client getClient(String clientId) throws ClientNotFoundException {
        return clientsDao.findByClientId(clientId);
    }

}
