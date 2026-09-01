package com.swetlokognatsk.authorization_server;

public enum Clients {
    CLIENT_1("CLIENT_ID_1", "CLIENT_SECRET_1", new String[] { "foo", "bar" }, "");

    public final String clientId;
    public final String clientSecret;
    public final String[] scopes;
    public final String redirectURI;

    Clients(final String clientId, final String clientSecret, final String[] scopes, final String redirectURI) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scopes = scopes;
        this.redirectURI = redirectURI;
    }
}
