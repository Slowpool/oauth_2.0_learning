package com.swetlokognatsk.client.external_services;

public final class ProtectedResource {

    public static String getFetchResourceEndpoint() {
        return "http://protected-resource:8083/resource/fetch";
    }

    public static String getWordsListEndpoint() {
        return "http://protected-resource:8083/words";
    }

    public static String getAddWordEndpoint() {
        return getWordsListEndpoint();
    }

    public static String getRemoveWordEndpoint() {
        return getWordsListEndpoint();
    }

}
