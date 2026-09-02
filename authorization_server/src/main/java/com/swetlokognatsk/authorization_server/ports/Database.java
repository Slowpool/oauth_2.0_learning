package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.authorization_server.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.models.Client;

public interface Database {
    public Client getClient(String clientId) throws ClientNotFoundException;
}
