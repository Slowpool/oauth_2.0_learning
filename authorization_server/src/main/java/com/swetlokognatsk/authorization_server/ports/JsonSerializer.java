package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;

public interface JsonSerializer {
    String serializeAuthorizationRequest(AuthorizationRequest authorizationRequest);

    AuthorizationRequest deserializeAuthorizationRequest(String authorizationRequestJson);

    String serializeAuthorizationCode(AuthorizationCode authorizationCode);

    AuthorizationCode deserializeAuthorizationCode(String authorizationCodeJson);
}
