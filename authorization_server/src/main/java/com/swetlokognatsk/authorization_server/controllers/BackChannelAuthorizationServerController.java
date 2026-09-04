package com.swetlokognatsk.authorization_server.controllers;

import java.util.Base64;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.swetlokognatsk.authorization_server.exceptions.InvalidAuthCredentialsFormatException;
import com.swetlokognatsk.authorization_server.ports.Database;

@RestController
public class BackChannelAuthorizationServerController {

    private static final String BASIC_AUTH_START = "Basic ";

    private final ApplicationContext ctx;
    private final Database database;

    public BackChannelAuthorizationServerController(final ApplicationContext ctx, final Database database) {
        this.ctx = ctx;
        this.database = database;
    }

    @RequestMapping("/token")
    public ResponseEntity<?> getToken(@RequestHeader(name = "Authorization", required = false) final String authHeader) {
        if (hasAuthHeader(authHeader)) {
            try {
                var authCredentials = decodeAuthCredentials(authHeader);
                // latch
                return ResponseEntity.ok("ok");
            } catch (InvalidAuthCredentialsFormatException e) {
                return ResponseEntity.badRequest().body("invalid auth credentials format. expected format: \"Authorization: Basic clientId:clientSecret\"");
            }
        } else {
            // latch
            return ResponseEntity.ok("not header");
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

    private static record AuthCredentials(String clientId, String clientSecret) {

    }
}
