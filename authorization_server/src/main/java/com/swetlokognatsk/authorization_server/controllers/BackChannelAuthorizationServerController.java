package com.swetlokognatsk.authorization_server.controllers;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String getToken(@RequestHeader (name = "Authorization", required = false) final String authHeader) {
        if (hasAuthHeader(authHeader)) {
            var authCredentials = decodeAuthCredentials(authHeader);
        }
        return "token";
    }

    private boolean hasAuthHeader(final String authHeader) {
        return authHeader != null && authHeader.length() > 0 && authHeader.startsWith(BASIC_AUTH_START);
    }

    private AuthCredentials decodeAuthCredentials(final String authHeader) {
        var encodedCredentials = authHeader.substring(BASIC_AUTH_START.length());
        var decodedCredentials = encodedCredentials
        var clientId = 
    }

    private static record AuthCredentials(String clientId, String clientSecret) {

    }
}
