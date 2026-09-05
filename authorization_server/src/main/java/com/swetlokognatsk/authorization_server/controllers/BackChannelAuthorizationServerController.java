package com.swetlokognatsk.authorization_server.controllers;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.ResponseEntity.*;
import com.swetlokognatsk.authorization_server.AuthorizationServerApplication;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationCodeNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidAuthCredentialsException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidAuthCredentialsFormatException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidClientIdException;
import com.swetlokognatsk.authorization_server.exceptions.UnknownGrantTypeException;
import com.swetlokognatsk.authorization_server.models.AccessTokenBody;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.authorization_server.models.RefreshTokenBody;
import com.swetlokognatsk.authorization_server.models.RefreshTokenResponse;
import com.swetlokognatsk.authorization_server.ports.AccessTokenGenerator;
import com.swetlokognatsk.authorization_server.ports.ClientSecretHasher;
import com.swetlokognatsk.authorization_server.ports.Database;
import com.swetlokognatsk.authorization_server.ports.RefreshTokenGenerator;
import com.swetlokognatsk.oauth_db.RefreshTokenNotFoundException;
import com.swetlokognatsk.oauth_db.models.AccessToken;
import com.swetlokognatsk.oauth_db.models.RefreshAndAccessTokensPair;
import com.swetlokognatsk.oauth_db.models.RefreshToken;
import com.swetlokognatsk.oauth_db.models.RefreshTokenValue;
import static com.swetlokognatsk.authorization_server.AuthorizationServerApplication.*;

@RestController
public class BackChannelAuthorizationServerController {

    private static final String BASIC_AUTH_START = "Basic ";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final List<String> KNOWN_GRANT_TYPES = List.of(AUTHORIZATION_CODE, REFRESH_TOKEN);

    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private static final int ACCESS_TOKEN_EXPIRES_IN = 3600;
    private static final int REFRESH_TOKEN_EXPIRES_IN = 3600;

    private final ApplicationContext ctx;
    private final Database database;
    private final ClientSecretHasher clientSecretHasher;
    private final AccessTokenGenerator accessTokenGenerator;
    private final RefreshTokenGenerator refreshTokenGenerator;

    public BackChannelAuthorizationServerController(final ApplicationContext ctx, final Database database, final ClientSecretHasher clientSecretHasher, final AccessTokenGenerator accessTokenGenerator, final RefreshTokenGenerator refreshTokenGenerator) {
        this.ctx = ctx;
        this.database = database;
        this.clientSecretHasher = clientSecretHasher;
        this.accessTokenGenerator = accessTokenGenerator;
        this.refreshTokenGenerator = refreshTokenGenerator;
    }

    @RequestMapping("/token")
    public ResponseEntity<?> getToken(@RequestHeader(name = "Authorization", required = false) final String authHeader, @RequestParam(name = "grant_type") final String grantType, @RequestParam(name = AUTHORIZATION_CODE, required = false) final String authorizationCode, @RequestParam(name = REFRESH_TOKEN, required = false) final String refreshToken) {
        if (hasAuthHeader(authHeader)) {
            try {
                var authCredentials = decodeAuthCredentials(authHeader);
                validateAuthCredentials(authCredentials);
                var clientId = authCredentials.clientId;
                validateGrantType(grantType);
                return switch (grantType) {
                case AUTHORIZATION_CODE -> getTokenByAuthorizationCode(clientId, authorizationCode);
                case REFRESH_TOKEN -> getTokenByRefreshToken(clientId, new RefreshTokenValue(refreshToken));
                default -> throw new RuntimeException("unknown grant type");
                };
            } catch (InvalidAuthCredentialsFormatException e) {
                return badRequest().body("invalid auth credentials format. expected format: \"Authorization: Basic clientId:clientSecret\"");
            } catch (InvalidAuthCredentialsException e) {
                return status(401).body("invalid auth credentials");
            } catch (UnknownGrantTypeException e) {
                return unprocessableContent().body("unknown grant type: %s".formatted(grantType));
            }
        } else {
            return status(401).body("auth credentials header is not found in request");
        }
    }

    private boolean hasAuthHeader(final String authHeader) {
        return authHeader != null && authHeader.length() > 0 && authHeader.startsWith(BASIC_AUTH_START);
    }

    private AuthCredentials decodeAuthCredentials(final String authHeader) throws InvalidAuthCredentialsFormatException {
        var encodedCredentials = authHeader.substring(BASIC_AUTH_START.length());
        var decodedCredentialsBytes = Base64.getDecoder().decode(encodedCredentials);
        var decodedCredentials = new String(decodedCredentialsBytes).split(":");

        if (decodedCredentials.length != 2) {
            throw new InvalidAuthCredentialsFormatException();
        }

        var clientId = decodedCredentials[0];
        var clientSecret = decodedCredentials[1];
        return new AuthCredentials(clientId, clientSecret);
    }

    private void validateAuthCredentials(final AuthCredentials authCredentials) throws InvalidAuthCredentialsException {
        Client client;
        try {
            client = database.getClientByClientId(authCredentials.clientId);
        } catch (ClientNotFoundException e) {
            throw new InvalidAuthCredentialsException();
        }

        if (!hashedSecretsEqual(client, authCredentials)) {
            throw new InvalidAuthCredentialsException();
        }
    }

    private boolean hashedSecretsEqual(final Client client, final AuthCredentials authCredentials) {
        var hashedClientSecret = clientSecretHasher.hash(authCredentials.clientSecret);
        return client.getSecretHash().equals(hashedClientSecret);
    }

    private void validateGrantType(final String grantType) throws UnknownGrantTypeException {
        if (!isKnownGrantType(grantType)) {
            throw new UnknownGrantTypeException();
        }
    }

    private static boolean isKnownGrantType(final String grantType) {
        return KNOWN_GRANT_TYPES.contains(grantType);
    }

    private ResponseEntity<?> getTokenByAuthorizationCode(final String authClientId, final String authorizationCode) {
        try {
            validateAuthorizationCode(authorizationCode);
            var authorizationCodeEntity = popAuthorizationCode(authorizationCode);
            validateClientId(authClientId, authorizationCodeEntity);

            var accessToken = generateAccessToken(authClientId);
            saveAccessToken(accessToken);

            var body = switch (TOKEN_STRATEGY) {
            case SINGLE_ACCESS_TOKEN -> {
                yield buildAccessTokenBody(accessToken);
            }
            case REFRESH_AND_ACCESS_PAIR -> {
                var refreshToken = generateRefreshToken(authClientId);
                saveRefreshToken(refreshToken);
                yield buildRefreshAndAccessTokensBody(accessToken, refreshToken);
            }
            default -> throw new IllegalArgumentException("unknown token strategy: %s".formatted(TOKEN_STRATEGY));
            };
            return status(200).body(body);
        } catch (AuthorizationCodeNotFoundException e) {
            return badRequest().body("authorization code is not found: %s".formatted(authorizationCode));
        } catch (InvalidClientIdException e) {
            return badRequest().body("clientId divergency");
        }
    }

    private void validateAuthorizationCode(final String authorizationCode) throws AuthorizationCodeNotFoundException {
        database.getAuthorizationCode(authorizationCode);
    }

    private AuthorizationCode popAuthorizationCode(final String authorizationCode) {
        try {
            return database.popAuthorizationCode(authorizationCode);
        } catch (AuthorizationCodeNotFoundException e) {
            throw new RuntimeException("auth code is not found, though it must have been");
        }
    }

    private void validateClientId(final String authClientId, final AuthorizationCode authorizationCode) throws InvalidClientIdException {
        var codeClientId = authorizationCode.clientId();
        validateClientIds(authClientId, codeClientId);
    }

    private void validateClientId(final String authClientId, final RefreshToken refreshToken) throws InvalidClientIdException {
        var refreshTokenClientId = refreshToken.getClientId();
        try {
            var client = database.getClientById(refreshTokenClientId);
            validateClientIds(authClientId, client.getClientId());
        } catch (ClientNotFoundException e) {
            throw new InvalidClientIdException();
        }
    }

    private void validateClientIds(final String authClientId, final String anotherClientId) throws InvalidClientIdException {
        if (!authClientId.equals(anotherClientId)) {
            throw new InvalidClientIdException();
        }
    }

    private AccessToken generateAccessToken(final String clientId) {
        try {
            var client = database.getClientByClientId(clientId);
            var newAccessTokenValue = accessTokenGenerator.generateAccessToken();
            return new AccessToken(newAccessTokenValue, client.getId(), LocalDateTime.now(), ACCESS_TOKEN_EXPIRES_IN);
        } catch (ClientNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // DRY! though it's learning project
    private RefreshToken generateRefreshToken(final String clientId) {
        try {
            var client = database.getClientByClientId(clientId);
            var newRefreshTokenValue = refreshTokenGenerator.generateRefreshToken();
            return new RefreshToken(newRefreshTokenValue, client.getId(), LocalDateTime.now(), REFRESH_TOKEN_EXPIRES_IN);
        } catch (ClientNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveAccessToken(final AccessToken accessToken) {
        database.saveAccessToken(accessToken);
    }

    private AccessTokenBody buildAccessTokenBody(final AccessToken accessToken) {
        var accessTokenValue = accessToken.getValue().value();
        return new AccessTokenBody(accessTokenValue, BEARER_TOKEN_TYPE, accessToken.getExpiresIn());
    }

    private RefreshTokenBody buildRefreshAndAccessTokensBody(final AccessToken accessToken, final RefreshToken refreshToken) {
        var accessTokenBody = buildAccessTokenBody(accessToken);
        var refreshTokenResponse = new RefreshTokenResponse(refreshToken.getValue().value(), refreshToken.getExpiresIn());
        return new RefreshTokenBody(accessTokenBody, refreshTokenResponse);
    }

    private void saveRefreshToken(final RefreshToken refreshToken) {
        database.saveRefreshToken(refreshToken);
    }

    private ResponseEntity<?> getTokenByRefreshToken(final String authClientId, final RefreshTokenValue refreshTokenValue) {
        if (refreshTokenValue == null) {
            return badRequest().body("refresh token is not found in request");
        }

        try {
            // TODO maybe pop?
            var refreshToken = database.getRefreshToken(refreshTokenValue);

            // TODO here i stopped
            validateClientId(authClientId, refreshToken);
        } catch (RefreshTokenNotFoundException e) {
            return status(401).body("refresh token is not found: %s".formatted(refreshTokenValue));
        } catch (InvalidClientIdException e) {
            try {
                database.removeRefreshToken(refreshTokenValue);
            } catch (RefreshTokenNotFoundException innerE) {
                e.addSuppressed(innerE);
            }

        }
    }

    private static record AuthCredentials(String clientId, String clientSecret) {
    }
}
