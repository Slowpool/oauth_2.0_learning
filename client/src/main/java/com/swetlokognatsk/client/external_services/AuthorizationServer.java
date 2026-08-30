package com.swetlokognatsk.client.external_services;

public final class AuthorizationServer {

    private AuthorizationServer() {
    }

    /** external - port is for resource owner */
    public static String getExternalAuthorizationEndpoint() {
        return "http://localhost:1938/authorize";
    }

    /** external - port is for inner container's communication */
    public static String getInternalAuthorizationEndpoint() {
        return "http://authorization-server:8081/authorize";
    }

    public static String getExternalTokenEndpoint() {
        return "http://localhost:1938/token";
    }

    public static String getInternalTokenEndpoint() {
        return "http://authorization-server:8081/token";
    }
}
