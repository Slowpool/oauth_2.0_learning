package com.swetlokognatsk.protected_resource.ports;

import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.models.AccessToken;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;

public interface Database {

    AccessToken findAccessToken(final AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException;

    void saveAccessToken(final AccessToken accessToken);

    void deleteAccessToken(final AccessTokenValue accessTokenValue);

    void deleteAll();
}
