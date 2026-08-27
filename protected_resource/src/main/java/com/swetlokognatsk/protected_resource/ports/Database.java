package com.swetlokognatsk.protected_resource.ports;

import com.swetlokognatsk.protected_resource.AccessToken;
import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;

public interface Database {

    AccessToken findAccessToken(final String accessToken) throws AccessTokenNotFoundException;

    void saveAccessToken(final AccessToken accessToken);
    
    void deleteAccessToken(final AccessToken accessToken);

    void deleteAll();
}
