package com.swetlokognatsk.authorization_server.models;

import java.io.Serializable;
import java.util.List;

public record AuthorizationCode(String requestId, String code, String clientId, List<String> scopes) implements Serializable {

}
