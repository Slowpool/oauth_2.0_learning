package com.swetlokognatsk.protected_resource.services;

import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;
import com.swetlokognatsk.protected_resource.ports.Database;

public final class AccessTokenVerifier {

    private final Database database;

    public AccessTokenVerifier(final Database database) {
        this.database = database;
    }

    public void verifyAccessToken(final AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException {
        var accessToken = database.findAccessToken(accessTokenValue);

    }
}
