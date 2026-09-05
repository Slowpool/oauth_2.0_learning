package com.swetlokognatsk.client.model;

import java.io.Serializable;

public final class AccessToken extends Token implements Serializable {

    public final String value;
    public final String type;
    public final int expiresIn;

    // TODO do i need type here?
    public AccessToken(final String accessToken, final String type, final int expiresIn) {
        this.value = accessToken;
        this.type = type;
        this.expiresIn = expiresIn;
    }

    public String toString() {
        return "accessToken: %s, type: %s, expiresIn: %s".formatted(value, type, expiresIn);
    }

}
