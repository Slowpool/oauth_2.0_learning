package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.authorization_server.models.AuthorizationCode;

public interface AuthorizationCodesRepository {

    void save(AuthorizationCode authorizationCode);
}
