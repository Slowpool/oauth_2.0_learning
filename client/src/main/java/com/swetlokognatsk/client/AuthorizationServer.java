package com.swetlokognatsk.client;

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
