package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.oauth_db.models.RefreshTokenValue;

public interface RefreshTokenGenerator {

    RefreshTokenValue generateRefreshToken();
}
