package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.stereotype.Repository;

import com.swetlokognatsk.authorization_server.daos.ClientsDao;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.authorization_server.ports.AuthorizationRequestsRepository;
import com.swetlokognatsk.authorization_server.ports.Database;

@Repository
public class JpaDatabase implements Database {

    private final ClientsDao clientsDao;
    private final AuthorizationRequestsRepository authorizationRequestsRepository;

    public JpaDatabase(final ClientsDao clientsDao, final AuthorizationRequestsRepository authorizationRequestsRepository) {
        this.clientsDao = clientsDao;
        this.authorizationRequestsRepository = authorizationRequestsRepository;
    }

    public Client getClient(final String clientId) throws ClientNotFoundException {
        return clientsDao.findByClientId(clientId);
    }

    public void saveAuthorizationRequest(final AuthorizationRequest authorizationRequest) {
        authorizationRequestsRepository.save(authorizationRequest);
    }

    public AuthorizationRequest getAuthorizationRequest(final String key) throws AuthorizationRequestNotFoundException {
        return authorizationRequestsRepository.findByKey(key);
    }

    public AuthorizationRequest popAuthorizationRequest(final String key) throws AuthorizationRequestNotFoundException {
        return authorizationRequestsRepository.popByKey(key);
    }
}
