package com.swetlokognatsk.authorization_server.models;

public record RefreshTokenBody(AccessTokenBody accessToken, RefreshTokenResponse refreshToken) {

}
