package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.stereotype.Repository;
import com.swetlokognatsk.authorization_server.daos.ClientsDao;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationCodeNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.authorization_server.ports.AuthorizationCodesRepository;
import com.swetlokognatsk.authorization_server.ports.AuthorizationRequestsRepository;
import com.swetlokognatsk.authorization_server.ports.Database;
import com.swetlokognatsk.oauth_db.RefreshTokenNotFoundException;
import com.swetlokognatsk.oauth_db.daos.AccessTokensDao;
import com.swetlokognatsk.oauth_db.daos.RefreshTokensDao;
import com.swetlokognatsk.oauth_db.models.AccessToken;
import com.swetlokognatsk.oauth_db.models.RefreshToken;
import com.swetlokognatsk.oauth_db.models.RefreshTokenValue;

@Repository
public class JpaDatabase implements Database {

    private final ClientsDao clientsDao;
    private final AuthorizationRequestsRepository authorizationRequestsRepository;
    private final AuthorizationCodesRepository authorizationCodesRepository;
    private final AccessTokensDao accessTokensDao;
    private final RefreshTokensDao refreshTokensDao;

    public JpaDatabase(final ClientsDao clientsDao, final AuthorizationRequestsRepository authorizationRequestsRepository, final AuthorizationCodesRepository authorizationCodesRepository, final AccessTokensDao AccessTokensDao, final RefreshTokensDao refreshTokensDao) {
        this.clientsDao = clientsDao;
        this.authorizationRequestsRepository = authorizationRequestsRepository;
        this.authorizationCodesRepository = authorizationCodesRepository;
        this.accessTokensDao = AccessTokensDao;
        this.refreshTokensDao = refreshTokensDao;
    }

    public Client getClientByClientId(final String clientId) throws ClientNotFoundException {
        return clientsDao.findByClientId(clientId);
    }

    public Client getClientById(int id) throws ClientNotFoundException {
        return clientsDao.findById(id);
    }

    public void saveAuthorizationRequest(final AuthorizationRequest authorizationRequest) {
        authorizationRequestsRepository.save(authorizationRequest);
    }

    public AuthorizationRequest getAuthorizationRequest(final String key) throws AuthorizationRequestNotFoundException {
        return authorizationRequestsRepository.findByRequestId(key);
    }

    public AuthorizationRequest popAuthorizationRequest(final String key) throws AuthorizationRequestNotFoundException {
        return authorizationRequestsRepository.popByRequestId(key);
    }

    public void saveAuthorizationCode(final AuthorizationCode authorizationCode) {
        authorizationCodesRepository.save(authorizationCode);
    }

    public AuthorizationCode getAuthorizationCode(final String authorizationCode) throws AuthorizationCodeNotFoundException {
        return authorizationCodesRepository.findByCode(authorizationCode);
    }

    public AuthorizationCode popAuthorizationCode(final String authorizationCode) throws AuthorizationCodeNotFoundException {
        return authorizationCodesRepository.popByCode(authorizationCode);
    }

    public void saveAccessToken(final AccessToken accessToken) {
        accessTokensDao.save(accessToken);
    }

    public void saveRefreshToken(final RefreshToken refreshToken) {
        refreshTokensDao.save(refreshToken);
    }

    public RefreshToken getRefreshToken(final RefreshTokenValue refreshTokenValue) throws RefreshTokenNotFoundException {
        return refreshTokensDao.findByValue(refreshTokenValue);
    }

    public void removeRefreshToken(RefreshTokenValue refreshTokenValue) throws RefreshTokenNotFoundException {
        refreshTokensDao.remove(refreshTokenValue);
    }

}
