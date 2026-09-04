package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.authorization_server.exceptions.AuthorizationCodeNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;

public interface AuthorizationCodesRepository {

    void save(AuthorizationCode authorizationCode);

    AuthorizationCode findByCode(String authorizationCode) throws AuthorizationCodeNotFoundException;

    AuthorizationCode popByCode(String authorizationCode) throws AuthorizationCodeNotFoundException;
}
