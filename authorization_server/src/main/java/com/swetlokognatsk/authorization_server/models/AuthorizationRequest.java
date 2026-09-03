package com.swetlokognatsk.authorization_server.models;

import java.io.Serializable;

public final record AuthorizationRequest(String id, String clientId, String redirectUri, String responseType) implements Serializable {

}
