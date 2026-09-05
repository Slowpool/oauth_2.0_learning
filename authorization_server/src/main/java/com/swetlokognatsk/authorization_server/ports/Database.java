package com.swetlokognatsk.authorization_server.ports;

import com.swetlokognatsk.authorization_server.exceptions.AuthorizationCodeNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.oauth_db.RefreshTokenNotFoundException;
import com.swetlokognatsk.oauth_db.models.AccessToken;
import com.swetlokognatsk.oauth_db.models.RefreshToken;
import com.swetlokognatsk.oauth_db.models.RefreshTokenValue;

public interface Database {
    Client getClientByClientId(String clientId) throws ClientNotFoundException;

    Client getClientById(int id) throws ClientNotFoundException;

    void saveAuthorizationRequest(AuthorizationRequest authorizationRequest);

    AuthorizationRequest getAuthorizationRequest(String key) throws AuthorizationRequestNotFoundException;

    AuthorizationRequest popAuthorizationRequest(String key) throws AuthorizationRequestNotFoundException;

    void saveAuthorizationCode(AuthorizationCode authorizationCode);

    AuthorizationCode getAuthorizationCode(String authorizationCode) throws AuthorizationCodeNotFoundException;

    AuthorizationCode popAuthorizationCode(String authorizationCode) throws AuthorizationCodeNotFoundException;

    void saveAccessToken(AccessToken accessToken);

    void saveRefreshToken(RefreshToken refreshToken);

    RefreshToken getRefreshToken(RefreshTokenValue refreshTokenValue) throws RefreshTokenNotFoundException;

    void removeRefreshToken(RefreshTokenValue refreshTokenValue) throws RefreshTokenNotFoundException;

}
