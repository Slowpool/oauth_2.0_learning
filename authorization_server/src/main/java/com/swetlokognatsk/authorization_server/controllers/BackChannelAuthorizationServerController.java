package com.swetlokognatsk.authorization_server.controllers;

import java.util.Base64;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.ResponseEntity.*;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationCodeNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidAuthCredentialsException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidAuthCredentialsFormatException;
import com.swetlokognatsk.authorization_server.exceptions.UnknownGrantTypeException;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.authorization_server.ports.ClientSecretHasher;
import com.swetlokognatsk.authorization_server.ports.Database;

@RestController
public class BackChannelAuthorizationServerController {

    private static final String BASIC_AUTH_START = "Basic ";
    private static final List<String> KNOWN_GRANT_TYPES = List.of("authorization_code");

    private final ApplicationContext ctx;
    private final Database database;
    private final ClientSecretHasher clientSecretHasher;

    public BackChannelAuthorizationServerController(final ApplicationContext ctx, final Database database, final ClientSecretHasher clientSecretHasher) {
        this.ctx = ctx;
        this.database = database;
        this.clientSecretHasher = clientSecretHasher;
    }

    @RequestMapping("/token")
    public ResponseEntity<?> getToken(@RequestHeader(name = "Authorization", required = false) final String authHeader, @RequestParam(name = "grant_type") final String grantType, @RequestParam(name = "authorization_code") final String authorizationCode) {
        if (hasAuthHeader(authHeader)) {
            try {
                var authCredentials = decodeAuthCredentials(authHeader);
                validateAuthCredentials(authCredentials);
                validateGrantType(grantType);
                validateAuthorizationCode(authorizationCode);
                // latch
                return ok("ok");
            } catch (InvalidAuthCredentialsFormatException e) {
                return badRequest().body("invalid auth credentials format. expected format: \"Authorization: Basic clientId:clientSecret\"");
            } catch (InvalidAuthCredentialsException e) {
                return status(401).body("invalid auth credentials");
            } catch (UnknownGrantTypeException e) {
                return unprocessableContent().body("unknown grant type: %s".formatted(grantType));
            }
            catch (AuthorizationCodeNotFoundException e) {
                return badRequest().body("authorization code is not found: %s".formatted(authorizationCode));
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
            client = database.getClient(authCredentials.clientId);
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

    private void validateAuthorizationCode(final String authorizationCode) throws AuthorizationCodeNotFoundException {
        database.getAuthorizationCode(authorizationCode);
    }

    private static record AuthCredentials(String clientId, String clientSecret) {

    }
}
