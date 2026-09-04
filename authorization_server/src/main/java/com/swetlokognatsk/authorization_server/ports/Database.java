package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.authorization_server.exceptions.AuthorizationCodeNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.models.Client;

public interface Database {
    Client getClient(String clientId) throws ClientNotFoundException;

    void saveAuthorizationRequest(AuthorizationRequest authorizationRequest);

    AuthorizationRequest getAuthorizationRequest(String key) throws AuthorizationRequestNotFoundException;

    AuthorizationRequest popAuthorizationRequest(String key) throws AuthorizationRequestNotFoundException;

    void saveAuthorizationCode(AuthorizationCode authorizationCode);

    AuthorizationCode getAuthorizationCode(String authorizationCode) throws AuthorizationCodeNotFoundException;
}
