package com.swetlokognatsk.authorization_server.models;

import java.io.Serializable;

public record AuthorizationCode(String requestId, String code) implements Serializable {

}
