package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;

public interface AuthorizationRequestsRepository {
    void save(AuthorizationRequest authorizationRequest);

    AuthorizationRequest findByRequestId(String key) throws AuthorizationRequestNotFoundException;

    AuthorizationRequest popByRequestId(String key) throws AuthorizationRequestNotFoundException;

}
