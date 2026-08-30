package com.swetlokognatsk.client.external_services;

public final class ProtectedResource {

    public static String getFetchResourceEndpoint() {
        return "http://protected-resource:8083/resource/fetch";
    }
}
