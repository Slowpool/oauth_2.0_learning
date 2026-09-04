package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.oauth_db.models.AccessTokenValue;

public interface AccessTokenGenerator {

    AccessTokenValue generate();
}
