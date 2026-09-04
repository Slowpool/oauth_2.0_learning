package com.swetlokognatsk.authorization_server.ports;

public interface ClientSecretHasher {

    String hash(String clientSecret);
}
