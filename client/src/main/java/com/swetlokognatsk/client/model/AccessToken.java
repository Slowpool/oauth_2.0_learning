package com.swetlokognatsk.client.model;

import java.io.Serializable;

public final class AccessToken extends Token implements Serializable {

    public final String value;
    public final String type;
    
    // TODO do i need type here?
    public AccessToken(final String accessToken, final String type) {
        this.value = accessToken;
        this.type = type;
    }

    public String toString() {
        return "accessToken: %s, token type: %s".formatted(value, type);
    }

}
