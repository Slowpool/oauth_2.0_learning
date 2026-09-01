package com.swetlokognatsk.client;

public final class Client {

    private Client() {
    }

    public static String getId() {
        return "CLIENT_ID_1";
    }

    public static String getSecret() {
        return "CLIENT_SECRET_1";
    }

    public static String getRedirectURI() {
        return "http://localhost:1939/callback";
    }
}
