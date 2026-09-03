package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;

public interface JsonSerializer {
    String serializeAuthorizationRequest(AuthorizationRequest authorizationRequest);
    AuthorizationRequest deserializeAuthorizationRequest(String authorizationRequestJson);
}
