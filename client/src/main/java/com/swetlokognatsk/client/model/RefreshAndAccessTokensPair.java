package com.swetlokognatsk.client.model;

public final class RefreshAndAccessTokensPair extends Token {

    public final RefreshToken refreshToken;
    public final AccessToken accessToken;

    public RefreshAndAccessTokensPair(final RefreshToken refreshToken, final AccessToken accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
