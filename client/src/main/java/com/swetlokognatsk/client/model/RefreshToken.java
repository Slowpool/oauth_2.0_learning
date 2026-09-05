package com.swetlokognatsk.client.model;

public final class RefreshToken extends Token {

    public final String value;
    public final int expiresIn;

    public RefreshToken(final String refreshToken, final int expiresIn) {
        this.value = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String toString() {
        return value;
    }
}
