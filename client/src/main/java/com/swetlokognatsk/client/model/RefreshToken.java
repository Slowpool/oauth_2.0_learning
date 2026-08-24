package com.swetlokognatsk.client.model;

public final class RefreshToken extends Token {

    public final String value;

    public RefreshToken(final String value) {
        this.value = value;
    }
}
