package com.swetlokognatsk.client.external_services;

public final class AuthorizationServer {

    private AuthorizationServer() {
    }

    public static String getAuthorizationEndpoint() {
        return "http://localhost:1938/authorize";
    }

    public static String getTokenEndpoint() {
        return "http://localhost:1938/token";
    }
}
