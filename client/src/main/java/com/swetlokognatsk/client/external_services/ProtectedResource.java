package com.swetlokognatsk.client.external_services;

public final class ProtectedResource {

    public static String getFetchResourceEndpoint() {
        // TODO it must be localhost:1940
        return "http://protected-resource:8083/resource/fetch";
    }
}
